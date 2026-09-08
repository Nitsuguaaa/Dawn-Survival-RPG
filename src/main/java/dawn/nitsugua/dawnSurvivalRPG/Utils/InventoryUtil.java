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

    public static int countItems(Inventory inv, String itemId) {
        Material material = Material.matchMaterial(itemId);

        if(material != null && material != Material.AIR) {
            int count = 0;
            for(ItemStack item : inv.getStorageContents()) {
                if(item != null) {
                    if(item.getType() == material && !hasCustomId(item)) {
                        count += item.getAmount();
                    }
                }
            }
            return count;
        }

        int count = 0;
        for(ItemStack item : inv.getStorageContents()) {
            if(item != null && item.hasItemMeta()) {
                String id = item.getItemMeta().getPersistentDataContainer().get(DawnKeys.ITEM_ID, PersistentDataType.STRING);
                if(itemId.equals(id)) {
                    count += item.getAmount();
                }
            }
        }

        return count;
    }

    public static void removeItems(Inventory inv, String itemId, int amountToRemove) {
        int remaining = amountToRemove;
        Material material = Material.matchMaterial(itemId);
        ItemStack[] storage = inv.getStorageContents();

        for(int i = 0; i < storage.length; i++) {
            if(remaining <= 0) break;

            ItemStack item = inv.getItem(i);
            if(item == null) continue;

            boolean matches = (material != null && material != Material.AIR)
                    ? item.getType() == material && !hasCustomId(item) //if vanilla
                    : itemId.equals(getCustomId(item)); //if custom

            if(matches) {
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

    // --- Small helpers ---
    private static boolean hasCustomId(ItemStack item) {
        return item != null && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer()
                .has(DawnKeys.ITEM_ID, PersistentDataType.STRING);
    }

    private static String getCustomId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(DawnKeys.ITEM_ID, PersistentDataType.STRING);
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
