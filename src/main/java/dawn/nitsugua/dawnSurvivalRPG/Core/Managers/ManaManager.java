package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class ManaManager {
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    private final DawnSurvivalRPG plugin;

    public ManaManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void onManaItemUse(CustomItem customItem) {
        Double manaCost = customItem.getManaCost();

    }

    public void calculateMaxMana(Player player, @Nullable ItemStack item, @Nullable ItemStack previousItem) {
        //player.sendMessage("Calculating mana..."); //DEBUG
        //this would calculate armor but for now it's just weapons
        DawnPlayer dawnPlayer = dawnRegistry.getDawnPlayer(player.getUniqueId());

        if(DawnKeys.isDawnObject(item)) {
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            player.sendMessage("Current item has been received by mana manager");
            if(pdc.has(DawnKeys.MANA)) {
                double prevMana = pdc.get(DawnKeys.MANA, PersistentDataType.DOUBLE);
                dawnPlayer.addMana(prevMana);
            }
        }

        if(DawnKeys.isDawnObject(previousItem)) {
            PersistentDataContainer prevPdc = previousItem.getItemMeta().getPersistentDataContainer();
            player.sendMessage("Previous item has been received by mana manager");
            if(prevPdc.has(DawnKeys.MANA)) {
                double mana = prevPdc.get(DawnKeys.MANA, PersistentDataType.DOUBLE);
                dawnPlayer.removeMana(mana);
            }
        }

        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.MESSAGE, "Your current mana now is: " + dawnPlayer.getMana()));
    }
}
