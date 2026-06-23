package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class ManaManager {
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    private final DawnSurvivalRPG plugin;

    public ManaManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void onManaItemUse(CustomItem customItem, Player player) {
        DawnPlayer dawnPlayer = dawnRegistry.getDawnPlayer(player.getUniqueId());
        Double manaCost = customItem.getManaCost();

        if(manaCost == null) return;
        dawnPlayer.removeCurrentMana(manaCost);
        player.sendMessage("Removed mana, current: " + dawnPlayer.getCurrentMana());

    }

    public static void regenMana() {
        HashMap<UUID, DawnPlayer> dawnPlayers = DawnRegistry.getInstance().getAllPlayers();
        if (dawnPlayers == null) return;

        for(DawnPlayer dawnPlayer : dawnPlayers.values()) {
            //calculate any armor, weapon, enhancements that add more mana regeneration
            if(dawnPlayer.getCurrentMana() >= dawnPlayer.getMana()) continue; //skip if already at max mana cap
            dawnPlayer.addCurrentMana(1.0);
            Bukkit.getPlayer(dawnPlayer.getPlayerId()).sendMessage("Your mana is now: " + dawnPlayer.getCurrentMana());
        }
    }

    public void manaActionBar() {

    }

    public void calculateMaxMana(Player player, @Nullable ItemStack item, @Nullable ItemStack previousItem) {
        //player.sendMessage("Calculating mana..."); //DEBUG
        //this would calculate armor but for now it's just weapons
        DawnPlayer dawnPlayer = dawnRegistry.getDawnPlayer(player.getUniqueId());

        if(DawnKeys.isDawnObject(item)) {
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            //DEBUG: player.sendMessage("Current item has been received by mana manager");
            if(pdc.has(DawnKeys.MANA)) {
                double mana = pdc.getOrDefault(DawnKeys.MANA, PersistentDataType.DOUBLE, 0.0);
                if(mana > 0) {
                    dawnPlayer.addMana(mana);
                }
            }
        }

        if(DawnKeys.isDawnObject(previousItem)) {
            PersistentDataContainer prevPdc = previousItem.getItemMeta().getPersistentDataContainer();
            //DEBUG: player.sendMessage("Previous item has been received by mana manager");
            if(prevPdc.has(DawnKeys.MANA)) {
                double mana = prevPdc.getOrDefault(DawnKeys.MANA, PersistentDataType.DOUBLE, 0.0);
                if(mana > 0) {
                    dawnPlayer.removeMana(mana);
                    if(dawnPlayer.getCurrentMana() > dawnPlayer.getMana()) {
                        dawnPlayer.setCurrentMana(dawnPlayer.getMana());
                    }
                }
            }
        }

       //DEBUG:  player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "Your current mana now is: " + dawnPlayer.getMana()));
    }

    public boolean hasMana(Player player, PersistentDataContainer pdc) {
        DawnPlayer dawnPlayer = dawnRegistry.getDawnPlayer(player.getUniqueId());
        double currentMana = dawnPlayer.getCurrentMana();
        Double itemMana = pdc.get(DawnKeys.MANA, PersistentDataType.DOUBLE);
        if (itemMana == null) return false;

        if(itemMana > currentMana) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You don't have enough mana!"));
            return false;
        }
        return true;
    }
}
