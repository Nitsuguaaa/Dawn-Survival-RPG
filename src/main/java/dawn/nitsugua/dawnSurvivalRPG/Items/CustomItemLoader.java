package dawn.nitsugua.dawnSurvivalRPG.Items;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.Weapons.CustomWeapon;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomItemLoader {
    private final DawnSurvivalRPG plugin;

    public CustomItemLoader(@NotNull DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

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
                - for future reference if it does break constantly. Do add edge cases for missing YAML data :)
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

            try { //fallback try-catch in-case I'm dumb and I didn't uppercase my material
                parentMaterial = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLog().info(TextUtil.format("<red>Invalid material: " + materialName));
                return;
            }

            List<Component> lore = parseLore(config.getStringList("lore"));

            //plugin.getLog().info(TextUtil.format("<aqua>This YAML's name is: </aqua><white>" + config.getString("name")));

            switch(parentFileName) {
                case "weapons":
                    double damage = config.getDouble("weapon-damage");
                    int weaponDurability = 0;
                    if(config.getInt("weapon-durability") != 0) { //There is weapon-durability stated
                        weaponDurability = config.getInt("weapon-durability");
                    }

                    item = new CustomWeapon(customMaterial, rarity, name, parentMaterial, category, subCategory, damage, weaponDurability, lore, customMaterial);
                    plugin.getLog().info(TextUtil.format("<green>Registering Weapon"));
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
                CustomItemManager manager = new CustomItemManager(plugin);
                manager.registerItem(item);
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
                componentLore.add(TextUtil.format(line));
            }
        }

        return componentLore;
    }


    public static void reloadCustomItem() {
        //run something
        //has to check if the item it scanned already exists then just update it
    }
}
