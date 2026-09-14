package dawn.nitsugua.dawnSurvivalRPG.Listeners;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.InventoryUtil;
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
import org.bukkit.persistence.PersistentDataContainer;

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
        Material type = item.getType();

        // 1. THE BOUNCER: If it's a Dawn Custom Item, instantly ignore it!
        // This saves the server from doing ANY calculations on your custom gear.
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getPersistentDataContainer().has(DawnKeys.DAWN_OBJECT)) {
            return;
        }

        // 2. Now we only run the heavy logic on VANILLA items
        if(type == Material.ENCHANTED_BOOK || type.getMaxDurability() > 0) {

            if(meta != null) {
                // Since Mending is erased, this will only ever trigger if a vanilla
                // mob somehow drops a mending book (e.g. from a villager trading glitch)
                if(hasMending(meta)) {
                    if(type == Material.ENCHANTED_BOOK) {
                        e.getEntity().setItemStack(InventoryUtil.resolveItem(plugin, "COMMON_KEY_SHARD", 1));
                    } else {
                        meta.removeEnchant(Enchantment.MENDING);
                        item.setItemMeta(meta);
                        e.getEntity().setItemStack(item);
                    }
                }
            }

            // 3. ELYTRA PLUG (Only runs on Vanilla Elytras now!)
            if(type == Material.ELYTRA) {
                boolean isCustomElytra = (meta != null && meta.getPersistentDataContainer().has(DawnKeys.FIXED));

                if(!isCustomElytra) {
                    ItemStack brokenElytra = InventoryUtil.resolveItem(plugin, "BROKEN_ELYTRA", 1);
                    if(brokenElytra != null) {
                        e.getEntity().setItemStack(brokenElytra);
                    }
                }
            }
        }
    }

    private boolean hasMending(ItemMeta meta) {
        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            return storageMeta.hasStoredEnchant(Enchantment.MENDING);
        }
        return meta.hasEnchant(Enchantment.MENDING);
    }


}
