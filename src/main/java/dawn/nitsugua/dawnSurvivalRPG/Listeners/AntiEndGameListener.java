package dawn.nitsugua.dawnSurvivalRPG.Listeners;

import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class AntiEndGameListener implements Listener {

    @EventHandler
    public void onGlide(EntityToggleGlideEvent e) {
        if(!(e.getEntity() instanceof Player player)) return;

        if(e.isGliding()) {
            ItemStack chestplate = player.getInventory().getChestplate();

            if(chestplate != null && chestplate.getType() == Material.ELYTRA) {
                boolean isFixed = chestplate.getItemMeta().getPersistentDataContainer().has(DawnKeys.FIXED, PersistentDataType.BOOLEAN);

                if(!isFixed) {
                    e.setCancelled(true);

                    player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "It seems that this elytra is broken."));
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                }
            }
        }
    }

//    @EventHandler
//    public void onPrepareSmith(PrepareSmithingEvent e) {
//        if(e.getResult() == null) return;
//
//        if(isNetheriteGear(e.getResult().getType())) {
//            e.setResult(null);
//        }
//    }
//
//    @EventHandler
//    public void onSmithingClick(InventoryClickEvent e) {
//        if(e.getInventory().getType() == InventoryType.SMITHING) {
//            if(e.getSlotType() == InventoryType.SlotType.RESULT) {
//                if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
//                    ItemStack template = e.getInventory().getItem(0);
//                    if(template != null && template.getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
//                        e.setCancelled(true);
//                        e.getWhoClicked().sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "You somehow forgot the knowledge of netherite smithing."));
//
//                    }
//                }
//            }
//        }
//    }
//
//    private boolean isNetheriteGear(Material mat) {
//        return mat == Material.NETHERITE_SWORD || mat == Material.NETHERITE_PICKAXE ||
//                mat == Material.NETHERITE_AXE || mat == Material.NETHERITE_SHOVEL ||
//                mat == Material.NETHERITE_HOE || mat == Material.NETHERITE_HELMET ||
//                mat == Material.NETHERITE_CHESTPLATE || mat == Material.NETHERITE_LEGGINGS ||
//                mat == Material.NETHERITE_BOOTS;
//    }

}
