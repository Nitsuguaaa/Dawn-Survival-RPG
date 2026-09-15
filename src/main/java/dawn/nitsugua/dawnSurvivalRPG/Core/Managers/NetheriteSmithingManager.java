package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.InventoryUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class NetheriteSmithingManager {
    private final DawnSurvivalRPG plugin;

    private static final Map<Material, Material> UPGRADES = Map.of(
            Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
            Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE,
            Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS,
            Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS,
            Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
            Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
            Material.DIAMOND_HOE, Material.NETHERITE_HOE
    );

    public NetheriteSmithingManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        removeVanillaNetheriteUpgrades();
        registerDawnNetheriteUpgrades();
        registerElytra();
    }

    private void removeVanillaNetheriteUpgrades() {
        List<String> vanillaRecipeIds = List.of(
                "netherite_helmet_smithing",
                "netherite_chestplate_smithing",
                "netherite_leggings_smithing",
                "netherite_boots_smithing",
                "netherite_sword_smithing",
                "netherite_pickaxe_smithing",
                "netherite_axe_smithing",
                "netherite_shovel_smithing",
                "netherite_hoe_smithing",
                //extras
                "respawn_anchor",
                "tnt_minecart",
                "tnt",
                "end_crystal"
        );

        for(String id : vanillaRecipeIds) {
            NamespacedKey key = NamespacedKey.minecraft(id);
            Bukkit.removeRecipe(key);
        }
    }

    private void registerDawnNetheriteUpgrades() {
        ItemStack upgradeItem = InventoryUtil.resolveItem(plugin, "DECODED_NETHERITE_TEMPLATE", 1);
        if(upgradeItem == null) return;

        for(Map.Entry<Material, Material> entry : UPGRADES.entrySet()) {
            Material base = entry.getKey();
            Material result = entry.getValue();

            NamespacedKey key = new NamespacedKey(
                    plugin,
                    "dawn_netherite_upgrade_" + base.name().toLowerCase()
            );

            SmithingTransformRecipe recipe = new SmithingTransformRecipe(
              key,
              new ItemStack(result),
              new RecipeChoice.ExactChoice(upgradeItem),
              new RecipeChoice.MaterialChoice(base),
              new RecipeChoice.ExactChoice(new ItemStack(Material.NETHERITE_INGOT))
            );

            Bukkit.addRecipe(recipe);
        }
    }

    private void registerElytra() {
        NamespacedKey key = new NamespacedKey(plugin, "repaired_elytra");

        ItemStack repairedElytra = InventoryUtil.resolveItem(plugin, "REINFORCED_ELYTRA", 1);
        if(repairedElytra == null) return;
        ItemMeta meta = repairedElytra.getItemMeta();
        meta.getPersistentDataContainer().set(DawnKeys.FIXED, PersistentDataType.BOOLEAN, true);
        repairedElytra.setItemMeta(meta);

        ItemStack upgradeItem = InventoryUtil.resolveItem(plugin, "REINFORCED_STRING", 1);
        if(upgradeItem == null) return;

        ItemStack template = InventoryUtil.resolveItem(plugin, "DECODED_NETHERITE_TEMPLATE", 1);
        if(template == null) return;

        repairedElytra.setData(DataComponentTypes.MAX_DAMAGE, 1000);
        repairedElytra.setData(DataComponentTypes.DAMAGE, 0);

        ItemStack result = repairedElytra.clone();

        SmithingTransformRecipe recipe = new SmithingTransformRecipe(
                key,
                new ItemStack(result),
                new RecipeChoice.ExactChoice(template),
                new RecipeChoice.MaterialChoice(Material.ELYTRA),
                new RecipeChoice.ExactChoice(upgradeItem)
        );

        Bukkit.addRecipe(recipe);

    }


}
