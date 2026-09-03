package dawn.nitsugua.dawnSurvivalRPG.Utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class InventoryUtil {

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
}
