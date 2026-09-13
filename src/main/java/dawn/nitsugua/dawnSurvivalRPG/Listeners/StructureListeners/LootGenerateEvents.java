package dawn.nitsugua.dawnSurvivalRPG.Listeners.StructureListeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LootGenerateEvents implements Listener {

    private static final Map<Material, Integer> INFLATED_ITEMS = Map.ofEntries(
            // Ingots/Items (More common, slightly higher cap)
            Map.entry(Material.DIAMOND,       8),
            Map.entry(Material.EMERALD,       16),
            Map.entry(Material.IRON_INGOT,    32),
            Map.entry(Material.GOLD_INGOT,    32),
            Map.entry(Material.COAL,          32),
            Map.entry(Material.LAPIS_LAZULI,  32),
            Map.entry(Material.REDSTONE,      32),

            // Blocks (Extremely valuable, hard cap)
            Map.entry(Material.DIAMOND_BLOCK, 2),
            Map.entry(Material.EMERALD_BLOCK, 3),
            Map.entry(Material.IRON_BLOCK,    4),
            Map.entry(Material.GOLD_BLOCK,    4),
            Map.entry(Material.COAL_BLOCK,    8),
            Map.entry(Material.LAPIS_BLOCK,   8),
            Map.entry(Material.REDSTONE_BLOCK,8),

            Map.entry(Material.ENCHANTED_GOLDEN_APPLE, 1),  // God Apples. NEVER more than 1.
            Map.entry(Material.TOTEM_OF_UNDYING,       1),  // Totems. 1 per chest max.
            Map.entry(Material.GOLDEN_APPLE,           4),  // Regular Gapples
            Map.entry(Material.EXPERIENCE_BOTTLE,      16), // XP Bottles inflate levels
            Map.entry(Material.TNT,                    0),  // Grief tool if not protected
            Map.entry(Material.TRIDENT,               1),  // Extremely rare, keep it rare
            Map.entry(Material.ELYTRA,                0),  // YOU DISABLED ELYTRAS! Cap to 0 = remove entirely
            Map.entry(Material.NETHERITE_INGOT,        0),  // YOU DISABLED NETHERITE! Remove from loot
            Map.entry(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 0), // Same
            Map.entry(Material.SHULKER_BOX,            1)   // Portable storage is OP
    );

    private static final Map<Enchantment, Integer> ENCHANT_CAPS = Map.ofEntries(
            Map.entry(Enchantment.KNOCKBACK,          2),
            Map.entry(Enchantment.SHARPNESS,          5),
            Map.entry(Enchantment.POWER,              5),
            Map.entry(Enchantment.PROTECTION,         4),
            Map.entry(Enchantment.FIRE_ASPECT,        2),
            Map.entry(Enchantment.LOOTING,            3),
            Map.entry(Enchantment.FORTUNE,            3),
            Map.entry(Enchantment.EFFICIENCY,         5),
            Map.entry(Enchantment.UNBREAKING,         3),
            Map.entry(Enchantment.THORNS,             3),
            Map.entry(Enchantment.PUNCH,              2),
            Map.entry(Enchantment.FLAME,              1),
            Map.entry(Enchantment.INFINITY,           1),
            Map.entry(Enchantment.SILK_TOUCH,         1),
            Map.entry(Enchantment.SWEEPING_EDGE,      3),
            Map.entry(Enchantment.DEPTH_STRIDER,      3),
            Map.entry(Enchantment.FEATHER_FALLING,    4),
            Map.entry(Enchantment.FROST_WALKER,       2),
            Map.entry(Enchantment.SOUL_SPEED,         3),
            Map.entry(Enchantment.SWIFT_SNEAK,        3)
    );


    @EventHandler
    public void antiInflation(LootGenerateEvent e) {
        List<ItemStack> generatedLoot = e.getLoot();

        Iterator<ItemStack> iterator = generatedLoot.iterator();

        while (iterator.hasNext()) {
            ItemStack loot = iterator.next();

            Integer cap = INFLATED_ITEMS.get(loot.getType());
            if(cap != null && cap == 0) {
                iterator.remove();
                continue;
            }

            if(cap != null && loot.getAmount() > cap) {
                loot.setAmount(cap);
            }

            if(!loot.hasItemMeta()) continue;
            ItemMeta meta = loot.getItemMeta();

            Map<Enchantment, Integer> enchants = new HashMap<>(meta.getEnchants());
            for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                Enchantment ench = entry.getKey();
                Integer level = entry.getValue();
                Integer maxLevel = ENCHANT_CAPS.get(ench);
                if(maxLevel != null && level > maxLevel) {
                    meta.removeEnchant(ench);
                    meta.addEnchant(ench, maxLevel, true);
                }
            }

            loot.setItemMeta(meta);
        }
    }
}
