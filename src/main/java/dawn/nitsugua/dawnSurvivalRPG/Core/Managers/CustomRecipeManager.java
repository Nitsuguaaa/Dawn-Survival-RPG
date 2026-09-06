package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomRecipeManager {
    private final DawnSurvivalRPG plugin;
    private final LootManager lootManager;

    // NEW: Track all registered keys so we can delete them on reload
    private final List<NamespacedKey> registeredKeys = new ArrayList<>();

    public CustomRecipeManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.lootManager = plugin.getLootManager();
        loadRecipes();
    }

    private void loadRecipes() {
        File folder = new File(plugin.getDataFolder(), "recipes");
        if (!folder.exists()) folder.mkdirs();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            for (String recipeId : config.getKeys(false)) {
                try {
                    ConfigurationSection section = config.getConfigurationSection(recipeId);
                    if (section == null) continue;

                    // 1. Resolve the Result Item (FIXED: Reads amount from YAML)
                    int resultAmount = section.getInt("amount", 1);
                    ItemStack result = lootManager.resolveItem(recipeId, 1);
                    if (result == null) {
                        plugin.getLog().warn("Recipe result not found: " + recipeId);
                        continue;
                    }
                    result.setAmount(resultAmount);

                    NamespacedKey key = new NamespacedKey(plugin, recipeId.toLowerCase());

                    // 2. Check the Type! Default to SHAPED if not specified
                    String type = section.getString("type", "SHAPED").toUpperCase();

                    if (type.equals("SHAPELESS")) {
                        handleShapelessRecipe(key, result, section);
                    } else {
                        handleShapedRecipe(key, result, section);
                    }

                } catch (Exception e) {
                    plugin.getLog().warn("Failed to load recipe: " + recipeId + " (" + e.getMessage() + ")");
                }
            }
        }
        plugin.getLog().info(TextUtil.format("<green>Custom Recipes Registered!</green>"));
    }

    private void handleShapedRecipe(NamespacedKey key, ItemStack result, ConfigurationSection section) {
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.setCategory(CraftingBookCategory.MISC);
        recipe.setGroup("dawn_custom_recipes");

        List<String> shape = section.getStringList("shape");
        if(shape.isEmpty() || shape.size() > 3) {
            plugin.getLog().warn("Invalid shape recipe for: " + key);
            return;
        }
        recipe.shape(shape.toArray(new String[0]));

        ConfigurationSection ingredients = section.getConfigurationSection("ingredients");
        if(ingredients != null) {
            for(String keyChar : ingredients.getKeys(false)) {
                if (keyChar.isEmpty()) continue; // <--- ADD THIS
                
                char c = keyChar.charAt(0);
                String itemKey = ingredients.getString(keyChar);

                if(itemKey == null || itemKey.equalsIgnoreCase("AIR")) continue;

                ItemStack ingredientStack = lootManager.resolveItem(itemKey, 1);
                if(ingredientStack != null) {
                    recipe.setIngredient(c, new RecipeChoice.ExactChoice(ingredientStack));
                } else {
                    Material mat = Material.matchMaterial(itemKey);
                    if(mat != null && mat != Material.AIR) {
                        recipe.setIngredient(c, mat);
                    }
                }
            }
        }
        plugin.getLog().info(TextUtil.format("<green>Custom Recipes Registered:<white> " + key));
        registeredKeys.add(key);
        Bukkit.addRecipe(recipe); //how does it know how much it needs?
    }

    private void handleShapelessRecipe(NamespacedKey key, ItemStack result, ConfigurationSection section) {
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);
        recipe.setCategory(CraftingBookCategory.MISC);
        recipe.setGroup("dawn_custom_recipes");

        ConfigurationSection ingredients = section.getConfigurationSection("ingredients");
        if(ingredients != null) {
            for(String itemKey : ingredients.getKeys(false)) {
                int amount = ingredients.getInt(itemKey, 1);

                if(itemKey.equalsIgnoreCase("AIR")) continue;

                ItemStack ingredientStack = lootManager.resolveItem(itemKey, 1);

                if(ingredientStack != null) {
                    RecipeChoice choice = new RecipeChoice.ExactChoice(ingredientStack);
                    for(int i = 0; i < amount; i++) {
                        recipe.addIngredient(choice);
                    }
                } else {
                    Material mat = Material.matchMaterial(itemKey);
                    if(mat != null && mat != Material.AIR) {
                        recipe.addIngredient(amount, mat);
                    }
                }
            }
        }
        plugin.getLog().info(TextUtil.format("<green>Custom Recipes Registered:<white> " + key));
        registeredKeys.add(key);
        Bukkit.addRecipe(recipe);
    }

    public void reload() {
        for(NamespacedKey key : registeredKeys) {
            Bukkit.removeRecipe(key);
        }

        registeredKeys.clear();
        loadRecipes();
    }
}
