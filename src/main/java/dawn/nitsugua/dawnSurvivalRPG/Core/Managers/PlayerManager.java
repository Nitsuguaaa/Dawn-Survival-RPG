package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;

import java.util.HashMap;
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
        DawnPlayer dawnPlayer = new DawnPlayer(playerId);
        dawnPlayer.setUsername(player.getName());
        dawnRegistry.registerPlayer(playerId, dawnPlayer);

        plugin.getBankDAO().loadBankAsync(playerId).thenAccept(bank -> {
           Bukkit.getScheduler().runTask(plugin, () -> {
               dawnPlayer.setBank(bank);
               //player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Bank data loaded! Balance: <yellow>" + bank.getBalance()));
           });
        });

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            initializePlayer(player);
        }, 1L);

        //plugin.getLog().info(TextUtil.format("<green>Player <white>" + player.getName() + " <green>joined in world: <aqua>" + player.getWorld().getName()));
        if(dungeonManager.isDungeonWorld(player.getWorld().getName())) dungeonManager.addPlayerToWorld(player.getWorld().getName());
    }

    public void playerLeaveEvent(Player player) {
        UUID playerId = player.getUniqueId();
        DawnPlayer dp = dawnRegistry.getDawnPlayer(playerId);

        if(dp != null && dp.getBank() != null) {
            if(dp.getBank().isDirty()) {
                plugin.getBankDAO().saveBankAsync(dp.getBank());
            }
        }

        dawnRegistry.removePlayer(playerId);
        if(dungeonManager.isDungeonWorld(player.getWorld().getName())) dungeonManager.removePlayerFromWorld(player.getWorld().getName());
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
