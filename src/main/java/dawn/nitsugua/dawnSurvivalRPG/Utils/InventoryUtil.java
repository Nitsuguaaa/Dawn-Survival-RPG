package dawn.nitsugua.dawnSurvivalRPG.Utils;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class InventoryUtil {

    /**
     * TODO: consider player inventory and armor/offhand slots
     * @param inv
     * @param itemId
     * @return
     */

    public static int countCustomItems(Inventory inv, String itemId) {
        int count = 0;
        for(ItemStack item : inv.getContents()) {
            if(item != null && item.hasItemMeta()) {
                PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
                if(pdc.has(DawnKeys.ITEM_ID, PersistentDataType.STRING)) {
                    String id = pdc.get(DawnKeys.ITEM_ID, PersistentDataType.STRING);
                    if(id.equals(itemId)) {
                        count += item.getAmount();
                    }
                }
            }
        }
        return count;
    }

    public static void removeCustomItems(Inventory inv, String itemId, int amountToRemove) {
        int remaining = amountToRemove;
        for(int i = 0; i < inv.getSize(); i++) {
            if(remaining <= 0) break;

            ItemStack item = inv.getItem(i);
            if(item != null && item.hasItemMeta()) {
                PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
                if(pdc.has(DawnKeys.ITEM_ID, PersistentDataType.STRING)) {
                    String id = pdc.get(DawnKeys.ITEM_ID, PersistentDataType.STRING);
                    if(itemId.equals(id)) {
                        int itemAmount = item.getAmount();
                        if(itemAmount > remaining) {
                            item.setAmount(itemAmount - remaining);
                            inv.setItem(i, item);
                            remaining = 0;
                        } else {
                            remaining -= itemAmount;
                            inv.setItem(i, null);
                        }
                    }
                }
            }
        }
    }

    public static ItemStack resolveItem(DawnSurvivalRPG plugin, String key, int amount) {
        ItemStack custom = plugin.getCustomItemFactory().createCustomItem(key, amount);
        if(custom != null) return custom;

        Material material = Material.matchMaterial(key);
        if(material != null) return new ItemStack(material, amount);

        plugin.getLog().info(TextUtil.sendLogInfo("LootManager", TextUtil.LogInfo.ERROR,"Unknown loot item: <white>" + key));
        return null;
    }
}
