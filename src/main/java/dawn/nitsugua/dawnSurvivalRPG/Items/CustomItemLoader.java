package dawn.nitsugua.dawnSurvivalRPG.Items;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.Weapons.CustomWeapon;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomItemLoader {
    //private final DawnSurvivalRPG plugin;
    private static final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    //public CustomItemLoader(@NotNull DawnSurvivalRPG plugin) { } //nothing for now

    //Do I just need separation of methods (static) or I want to use it elsewhere? (instance)
    public static void initialize(@NotNull DawnSurvivalRPG plugin) {
        File customItemFolder = new File(plugin.getDataFolder().getPath() + "/items"); //Items directory
        File[] files = customItemFolder.listFiles(); //Inside Items directory

        if (files == null) { plugin.getLog().info(TextUtil.format("<red>An error occurred while parsing the YAML files of custom items")); return;}

        //plugin.getLog().info(TextUtil.format("<aqua>Here are the files in the folder: "));
        for (File file : files) { //Each directory inside the Items gets opened and checked
            File[] subFiles = file.listFiles(); //the actual YAML

            if (subFiles == null) {plugin.getLog().info(TextUtil.format("<red>There seems to be no items in this directory")); return;}
            for (File subFile : subFiles) {
                parseFile(file.getName(), subFile, plugin);
            }
        }
    }

    private static void parseFile(String parentFileName, File file, DawnSurvivalRPG plugin) {
        //plugin.getLog().info(TextUtil.format("This YAML is inside: <yellow>" + parentFileName));


        /*
            TODO:

         */
        if (file.getName().endsWith(".yml")) {
            ConfigurationSection config = YamlConfiguration.loadConfiguration(file);

            CustomItem item = null;

            String customMaterial = config.getString("custom-material");
            // debug: plugin.getLog().info("[CustomItemLoader] Getting: " + customMaterial);
            String rarity = config.getString("rarity");
            String name = config.getString("name");
            //debug: plugin.getLog().info("[CustomItemLoader] Getting: " + name);
            String category = config.getString("category");
            String subCategory = config.getString("sub-category");

            String materialName = config.getString("material");
            Material parentMaterial;
            TextUtil.ItemRarity finalRarity;

            // Check for required fields
            if (customMaterial == null || name == null || materialName == null || rarity == null || category == null || subCategory == null) {
                plugin.getLog().info(TextUtil.format("<red>Missing required fields in: " + file.getName()));
                return;
            }

            try {
                finalRarity = TextUtil.ItemRarity.valueOf(rarity.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLog().info(TextUtil.format("<red>Invalid rarity: " + rarity));
                finalRarity = TextUtil.ItemRarity.COMMON;
            }

            try { //fallback try-catch in-case I'm dumb and I didn't uppercase my material
                parentMaterial = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLog().info(TextUtil.format("<red>Invalid material: " + materialName));
                return;
            }

            List<Component> lore = parseLore(config.getStringList("lore"));
            lore.add(TextUtil.setItemRarity(finalRarity, "<bold>" + rarity.toLowerCase() + " " + subCategory.toLowerCase()));

            //plugin.getLog().info(TextUtil.format("<aqua>This YAML's name is: </aqua><white>" + config.getString("name")));

            switch(parentFileName) {
                case "weapons":
                    double damage = config.getDouble("weapon-damage", 10.0);
                    if (damage <= 0) damage = 10.0; // Never allow 0 or negative
                    int weaponDurability = config.getInt("weapon-durability", 0); // Default 0

                    item = new CustomWeapon(customMaterial, finalRarity, name, parentMaterial, category, subCategory, damage, weaponDurability, lore, null);
                    plugin.getLog().info(TextUtil.sendLogInfo("CustomItemLoader", TextUtil.LogInfo.INFO, "Registering weapon: <white>" + name));
                    break;
                case "equipment":
                    plugin.getLog().info(TextUtil.format("<green>Registering Equipment"));
                    break;
                case "food":
                    plugin.getLog().info(TextUtil.format("<green>Registering Food"));
                    break;
                case "tools":
                    plugin.getLog().info(TextUtil.format("<green>Registering Tool"));
                    break;
                case "usable":
                    plugin.getLog().info(TextUtil.format("<green>Registering Usable item"));
                    break;
                default:
                    plugin.getLog().info(TextUtil.format("<red>An unknown directory is inside the items directory, please remove"));
            }

            if(item != null) {
                boolean success = dawnRegistry.registerItem(item);
                if(success) {
                    plugin.getLog().info(TextUtil.sendLogInfo("CustomItemLoader", TextUtil.LogInfo.INFO, "Custom Item registered!"));
                } else {
                    plugin.getLog().info(TextUtil.format("<red>An error occurred while registering the item"));
                }
            }
        }
    }

    //Method might change class if usability is frequent.
    public static List<Component> parseLore(List<String> lore) {
        List<Component> componentLore = new ArrayList<>();

        for(String line : lore) {
            if(line == null || line.isEmpty()) {
                componentLore.add(Component.empty());
            } else {
                componentLore.add(TextUtil.setItemLore(line));
            }
        }

        return componentLore;
    }


    public static void reloadCustomItem() {
        //run something
        //has to check if the item it scanned already exists then just update it
    }
}
