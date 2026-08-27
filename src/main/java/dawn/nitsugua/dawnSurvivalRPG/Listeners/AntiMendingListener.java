package dawn.nitsugua.dawnSurvivalRPG.Listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AntiMendingListener implements Listener {

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
}
