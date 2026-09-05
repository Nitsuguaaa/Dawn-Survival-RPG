package dawn.nitsugua.dawnSurvivalRPG.Listeners;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class AntiMendingListener implements Listener {
    private final DawnSurvivalRPG plugin;

    public AntiMendingListener(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFishEvent(PlayerFishEvent e) {
        if(e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if(e.getCaught() instanceof Item caughtItem) {
                ItemStack stack = caughtItem.getItemStack();
                if(stack.getType() == Material.ENCHANTED_BOOK) {
                    caughtItem.setItemStack(new ItemStack(Material.EMERALD, 1));
                }
            }
        }
    }

    @EventHandler
    public void onChestGenerate(LootGenerateEvent e) {
        e.getLoot().removeIf(stack -> stack.getType() == Material.ENCHANTED_BOOK);

        for(ItemStack stack : e.getLoot()) {
            if(stack.hasItemMeta()) {
                ItemMeta meta = stack.getItemMeta();

                if(meta.hasEnchant(Enchantment.MENDING)) {
                    meta.removeEnchant(Enchantment.MENDING);
                    stack.setItemMeta(meta);
                }
            }
        }
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent e) {
        EnchantmentOffer[] offers = e.getOffers();

        for(int i = 0; i < offers.length; i++) {
            EnchantmentOffer offer = offers[i];
            if(offer == null) return;

            if(offer.getEnchantment().equals(Enchantment.MENDING)) {
                offer.setEnchantment(Enchantment.SILK_TOUCH);
                offer.setCost(1 + (int) (Math.random() * 30));
            }
        }
    }

    @EventHandler
    public void onItemDrop(ItemSpawnEvent e) {
        ItemStack item = e.getEntity().getItemStack();

        if(isPotentiallyEnchanted(item.getType())) {
            if(hasMending(item)) {
                if(item.getType() == Material.ENCHANTED_BOOK) {
                    e.getEntity().setItemStack(plugin.getCustomItemFactory().createCustomItem("COMMON_KEY_SHARD", 1));
                } else {
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.removeEnchant(Enchantment.MENDING);
                    item.setItemMeta(itemMeta);
                    e.getEntity().setItemStack(item);
                }
            }
        }

    }

    private boolean isPotentiallyEnchanted(Material type) {
        return type == Material.ENCHANTED_BOOK ||
                type.name().contains("HELMET") ||
                type.name().contains("CHESTPLATE") ||
                type.name().contains("LEGGINGS") ||
                type.name().contains("BOOTS") ||
                type.name().contains("TUNIC") ||
                type.name().contains("SWORD") ||
                type.name().contains("PICKAXE") ||
                type.name().contains("AXE") ||
                type.name().contains("SHOVEL") ||
                type.name().contains("HOE") ||
                type.name().contains("BOW") ||
                type.name().contains("CROSSBOW") ||
                type == Material.FISHING_ROD ||
                type == Material.SHEARS ||
                type == Material.FLINT_AND_STEEL ||
                type == Material.CARROT_ON_A_STICK ||
                type == Material.WARPED_FUNGUS_ON_A_STICK;
    }

    private boolean hasMending(ItemStack item) {
        if(item == null || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if(meta instanceof EnchantmentStorageMeta storageMeta) {
            return storageMeta.hasStoredEnchant(Enchantment.MENDING);
        } else return meta.hasEnchant(Enchantment.MENDING);
    }


}
