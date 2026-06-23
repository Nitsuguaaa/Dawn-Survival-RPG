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
            if(dawnPlayer.getCurrentMana() >= dawnPlayer.getMaxMana()) continue; //skip if already at max mana cap

            double finalRegen = dawnPlayer.getMaxMana() * 0.02;
            dawnPlayer.addCurrentMana(finalRegen);
            Bukkit.getPlayer(dawnPlayer.getPlayerId()).sendMessage("Your mana is now: " + (int)dawnPlayer.getCurrentMana() + "/" + dawnPlayer.getMaxMana());
        }
    }

    public void manaActionBar() {

    }

    public void calculateMaxIntelligenceFromItem(Player player, @Nullable ItemStack item, @Nullable ItemStack previousItem) {
        //player.sendMessage("Calculating mana..."); //DEBUG
        //this would calculate armor but for now it's just weapons
        DawnPlayer dawnPlayer = dawnRegistry.getDawnPlayer(player.getUniqueId());

        if(DawnKeys.isDawnObject(item)) {
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            //DEBUG: player.sendMessage("Current item has been received by mana manager");
            if(pdc.has(DawnKeys.INTELLIGENCE)) {
                int intelligence = pdc.getOrDefault(DawnKeys.INTELLIGENCE, PersistentDataType.INTEGER, 0);
                if(intelligence > 0) {
                    //player.sendMessage("Set intelligence to: " + intelligence + ", max mana is now: " + dawnPlayer.getMaxMana()); //DEBUG
                    dawnPlayer.addIntelligence(intelligence);
                }
            }
        }

        if(DawnKeys.isDawnObject(previousItem)) {
            PersistentDataContainer prevPdc = previousItem.getItemMeta().getPersistentDataContainer();
            //DEBUG: player.sendMessage("Previous item has been received by mana manager");
            if(prevPdc.has(DawnKeys.INTELLIGENCE)) {
                int intelligence = prevPdc.getOrDefault(DawnKeys.INTELLIGENCE, PersistentDataType.INTEGER, 0);
                if(intelligence > 0) {
                    dawnPlayer.removeIntelligence(intelligence);
                    //player.sendMessage("Set intelligence to: " + intelligence + ", max mana is now: " + dawnPlayer.getMaxMana()); //DEBUG
                    if(dawnPlayer.getCurrentMana() > dawnPlayer.getMaxMana()) {
                        //player.sendMessage("Current mana is now: " + dawnPlayer.getCurrentMana()); //DEBUG
                        dawnPlayer.setCurrentMana(dawnPlayer.getMaxMana());
                    }
                }
            }
        }

       //DEBUG:  player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "Your current mana now is: " + dawnPlayer.getMana()));
    }

    public void calculateMaxIntelligenceFromArmor(Player player) {

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
