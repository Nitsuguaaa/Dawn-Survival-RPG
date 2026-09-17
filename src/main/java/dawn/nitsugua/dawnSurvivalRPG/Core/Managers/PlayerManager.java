package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Parties.Party;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.InventoryUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    private final ManaManager manaManager;
    private final DawnSurvivalRPG plugin;
    private final DungeonManager dungeonManager;

    public PlayerManager(DawnSurvivalRPG plugin, ManaManager manaManager) {
        this.plugin = plugin;
        this.manaManager = manaManager;
        this.dungeonManager = plugin.getDungeonManager();
    }

    public void playerJoinEvent(Player player) {
        UUID playerId = player.getUniqueId();

        plugin.getPlayerDAO().loadPlayerAsync(player).thenAcceptAsync(dawnPlayer -> {
            //dawnPlayer.setUsername(player.getName());
            dawnRegistry.registerPlayer(playerId, dawnPlayer);

            if(dawnPlayer.isFirstJoin()) Bukkit.getScheduler().runTask(plugin, () -> {runFirstJoin(player);});

            plugin.getBankDAO().loadBankAsync(playerId).thenAcceptAsync(bank -> {
               dawnPlayer.setBank(bank);

               Bukkit.getScheduler().runTask(plugin, () -> {
                   //runFirstJoin(player); //DEBUG: REMOVE AFTER TESTING
                  initializePlayer(player);
                  if(dungeonManager.isDungeonWorld(player.getWorld().getName())) {
                      dungeonManager.addPlayerToWorld(player.getWorld().getName());
                  }
               });
            }, plugin.getDatabaseManager().getDbExecutor());
        }, plugin.getDatabaseManager().getDbExecutor());
    }

    public void playerLeaveEvent(Player player) {
        UUID playerId = player.getUniqueId();
        DawnPlayer dp = dawnRegistry.getDawnPlayer(playerId);

        if(dp == null) {
            dawnRegistry.removePlayer(playerId);
            if (dungeonManager.isDungeonWorld(player.getWorld().getName())) {
                dungeonManager.removePlayerFromWorld(player.getWorld().getName());
            }
            return;
        }

        UUID partyId = dp.getPartyId();

        // THE FIX: Only look up the party if the player actually has a partyId!
        if (partyId != null) {
            Party party = dawnRegistry.getParty(partyId);

            if(party == null) {
                dp.removeParty();
            } else {
                boolean wasLeader = playerId.equals(party.getLeader());

                // Remove them first
                party.removeMember(playerId);

                // If they were the leader, transfer to someone else
                if (wasLeader && !party.getMembers().isEmpty()) {
                    UUID newLeaderId = party.getMembers().keySet().iterator().next();
                    party.setLeader(newLeaderId);

                    Player newLeader = Bukkit.getPlayer(newLeaderId);
                    if (newLeader != null) {
                        newLeader.sendMessage(MessagesUtil.sendInfo(
                                MessagesUtil.commonMessages.SUCCESS,
                                "You are now the party leader!"
                        ));
                    }

                    plugin.getPartyManager().broadcastToParty(party, "<white>" + newLeader.getName() + "<gray> is now the party leader.");
                }

                // Notify the party
                plugin.getPartyManager().broadcastToParty(party, "<white>" + player.getName() + "<gray> disconnected and left the party.");

                // Clean up empty parties
                if (party.getMembers().isEmpty()) {
                    dawnRegistry.removeParty(partyId);
                }
            }
        } else {
            // Defensive cleanup just in case they had a null partyId but thought they were in one
            dp.removeParty();
        }

        dp.setLastSeen(System.currentTimeMillis());

        // Null check for bank to prevent similar NPEs if bank loading failed
        if(dp.getBank() != null && dp.getBank().isDirty()) {
            plugin.getBankDAO().saveBankAsync(dp.getBank());
        }

        plugin.getPlayerDAO().savePlayerAsync(dp);
        dawnRegistry.removePlayer(playerId);

        if(dungeonManager.isDungeonWorld(player.getWorld().getName())) {
            dungeonManager.removePlayerFromWorld(player.getWorld().getName());
        }
    }

    public void playerSwitchWorldEvent(Player player, World from, World to) {
        String fromWorld = from.getName();
        String toWorld = to.getName();

        if(dungeonManager.isDungeonWorld(fromWorld)) {
            dungeonManager.removePlayerFromWorld(fromWorld);
        }

        if(dungeonManager.isDungeonWorld(toWorld)) {
            dungeonManager.addPlayerToWorld(toWorld);
        }
    }

    public void onPLayerDeath(PlayerDeathEvent e) {
        //economy
        Player player = e.getPlayer();
        ExcellentEconomyAPI execo = plugin.getEconomyAPI().getEconomyAPI();
        double balance = execo.getBalance(player, "money");

        if(balance <= 0) return;

        double lossPercentage;

        if(e.getDamageSource().getCausingEntity() instanceof Player) {
            lossPercentage = 0.20;
        } else {
            lossPercentage = 0.50;
        }

        double lossAmount = Math.round(balance * lossPercentage * 100.0) / 100.0;
        execo.withdraw(player, "money", lossAmount);

        String message = String.format("You died and lost %.2f coins.", lossAmount);
        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, message));

    }

    public void runFirstJoin(Player player) {
        for(Player p : plugin.getServer().getOnlinePlayers()) {
            p.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "<white>" + player.getName() + " <green>joined the server for the first time!"));
        }

        Inventory inv = player.getInventory();
        inv.addItem(
                InventoryUtil.resolveItem(plugin, "GP_CLAIM_SHOVEL", 1),
                InventoryUtil.resolveItem(plugin, "GP_CLAIM_INSPECTOR", 1),
                new ItemStack(Material.COOKED_BEEF, 32),
                new ItemStack(Material.STONE_SWORD),
                new ItemStack(Material.LEATHER_CHESTPLATE)
        );

        plugin.getEconomyAPI().getEconomyAPI().deposit(player, "money", 1000);
        plugin.getEconomyAPI().getEconomyAPI().deposit(player, "coins", 10);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "claimbook " + player.getName());

        MessagesUtil.sendServerInfo(player);

    }

    public void initializePlayer(Player player) {
        //get armor too but for now just main hand item

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        manaManager.calculateMaxIntelligenceFromItem(player, mainHand, null);
        //manaManager.calculateMaxIntelligenceFromArmor(player);

        DawnPlayer dawnPlayer = dawnRegistry.getDawnPlayer(player.getUniqueId());
        if (dawnPlayer == null) return;
        dawnPlayer.setCurrentMana(dawnPlayer.getMaxMana());
    }
}
