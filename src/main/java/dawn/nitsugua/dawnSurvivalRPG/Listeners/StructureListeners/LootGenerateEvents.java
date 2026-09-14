package dawn.nitsugua.dawnSurvivalRPG.Listeners.StructureListeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TrialSpawner;
import org.bukkit.block.Vault;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

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
            Map.entry(Material.SHULKER_BOX,            1),  // Portable storage is OP

            Map.entry(Material.BLAZE_POWDER, 32),
            Map.entry(Material.BLAZE_ROD, 10)
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
    public void chestGeneration(LootGenerateEvent e) {
        sanitizeLoot(e.getLoot());
    }

    @EventHandler
    public void vaultAndSpawnerGeneration(BlockDispenseLootEvent e) {
        Block block = e.getBlock();
        BlockState state = block.getState();

        if(state instanceof Vault || state instanceof TrialSpawner) {
            sanitizeLoot(e.getDispensedLoot());
        }
    }

    public static void sanitizeLoot(List<ItemStack> lootList) {
        // We don't even need the first loop anymore!
        Iterator<ItemStack> iterator = lootList.iterator();

        // Track exactly how many items we've allowed to pass through so far
        Map<Material, Integer> alreadyKept = new HashMap<>();

        while (iterator.hasNext()) {
            ItemStack loot = iterator.next();
            Material type = loot.getType();
            Integer cap = INFLATED_ITEMS.get(type);

            if (cap != null) {
                if (cap == 0) {
                    iterator.remove();
                    continue;
                }

                // 1. How much of this item have we already given them?
                int keptSoFar = alreadyKept.getOrDefault(type, 0);

                // 2. How much space is left under the cap?
                int spaceLeft = cap - keptSoFar;

                if (spaceLeft <= 0) {
                    iterator.remove(); // TOTAL CAP REACHED! Delete the stack!
                } else {
                    // 3. Only give them what fits in the remaining space
                    int amountToKeep = Math.min(loot.getAmount(), spaceLeft);
                    loot.setAmount(amountToKeep);

                    // 4. Add it to our running total
                    alreadyKept.put(type, keptSoFar + amountToKeep);
                }
            }

            // 3. Handle enchantments (Same as before)
            if (!loot.hasItemMeta()) continue;
            ItemMeta meta = loot.getItemMeta();

            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                Map<Enchantment, Integer> storedEnchants = new HashMap<>(storageMeta.getStoredEnchants());
                for (Map.Entry<Enchantment, Integer> entry : storedEnchants.entrySet()) {
                    Enchantment ench = entry.getKey();
                    int level = entry.getValue();
                    Integer maxLevel = ENCHANT_CAPS.get(ench);

                    if (maxLevel != null && level > maxLevel) {
                        storageMeta.removeStoredEnchant(ench);
                        storageMeta.addStoredEnchant(ench, maxLevel, true);
                    }
                }
                loot.setItemMeta(storageMeta);
            } else {
                Map<Enchantment, Integer> enchants = new HashMap<>(meta.getEnchants());
                for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                    Enchantment ench = entry.getKey();
                    int level = entry.getValue();
                    Integer maxLevel = ENCHANT_CAPS.get(ench);

                    if (maxLevel != null && level > maxLevel) {
                        meta.removeEnchant(ench);
                        meta.addEnchant(ench, maxLevel, true);
                    }
                }
                loot.setItemMeta(meta);
            }

            if (meta instanceof PotionMeta potionMeta) {
                // Loop through custom effects (like the ones in the JSON)
                for (PotionEffect effect : potionMeta.getCustomEffects()) {
                    // Get the internal name, e.g., "instant_health", "strength", "regeneration"
                    String effectName = effect.getType().getKey().getKey();
                    int amp = effect.getAmplifier(); // Amplifier 0 = Level I, Amplifier 5 = Level VI
                    int dur = effect.getDuration();

                    // 1. Cap Instant Health to Level II (Amplifier 1 = 8 hearts of healing)
                    if (effectName.equals("instant_health") && amp > 1) {
                        potionMeta.removeCustomEffect(effect.getType());
                        potionMeta.addCustomEffect(new PotionEffect(effect.getType(), 1, 1), true);
                    }
                    // 2. Cap Buffs (Regen, Strength, Speed, Haste, Resistance) to Level II (Amplifier 1)
                    else if ((effectName.equals("regeneration") || effectName.equals("strength") ||
                            effectName.equals("speed") || effectName.equals("swiftness") ||
                            effectName.equals("haste") || effectName.equals("resistance")) && amp > 1) {
                        potionMeta.removeCustomEffect(effect.getType());
                        // Keep the original duration, but nerf the level
                        potionMeta.addCustomEffect(new PotionEffect(effect.getType(), dur, 1), true);
                    }
                }
                loot.setItemMeta(potionMeta);
            }
        }
    }
}
