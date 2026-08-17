package dawn.nitsugua.dawnSurvivalRPG.Mobs;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.MobGroups;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomMobLoader {
    private static final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    public static void initialize(@NotNull DawnSurvivalRPG plugin) {
        plugin.getLog().info(TextUtil.sendLogInfo("CustomMobLoader", TextUtil.LogInfo.INFO, "Registering Custom Mobs..."));
        File customItemFolder = new File(plugin.getDataFolder().getPath() + "/mobs"); //Items directory
        File[] files = customItemFolder.listFiles(); //Inside Items directory

        if (files == null || files.length == 0) { plugin.getLog().info(TextUtil.format("<red>An error occurred while parsing the YAML files of custom items")); return;}

        //plugin.getLog().info(TextUtil.format("<aqua>Here are the files in the folder: "));
        for (File file : files) { //Each directory inside the Items gets opened and checked
            File[] subFiles = file.listFiles(); //the actual YAML

            if (subFiles == null || subFiles.length == 0) {plugin.getLog().info(TextUtil.format("<red>There seems to be no items in this directory")); return;}
            for (File subFile : subFiles) {
                parseFile(file.getName(), subFile, plugin);
            }
        }
        plugin.getLog().info(TextUtil.sendLogInfo("CustomMobLoader", TextUtil.LogInfo.SUCCESS, "Custom Mobs registered!"));
    }

    private static void parseFile(String parentFileName, File file, DawnSurvivalRPG plugin) {
        if (file.getName().endsWith(".yml")) {
            ConfigurationSection config = YamlConfiguration.loadConfiguration(file);

            CustomMob mob = null;
            List<CustomItem> drops = new ArrayList<>();

            String parentMobName = config.getString("parent-mob");
            String mobName = config.getString("name");
            ConfigurationSection dropsSection = config.getConfigurationSection("drops");

            // Check for required fields
            if (parentMobName == null || mobName == null || dropsSection == null) {
                plugin.getLog().info(TextUtil.format("<red>Missing required fields in: " + file.getName()));
                return;
            }

            // Convert parentMobName to NamespacedKey
            NamespacedKey parentMob = new NamespacedKey(plugin, parentMobName);

            // Parse drops
            for (String key : dropsSection.getKeys(false)) {
                ConfigurationSection drop = dropsSection.getConfigurationSection(key);
                String item = drop.getString("item");
                double chance = drop.getDouble("chance");
                int max = drop.getInt("max");

                CustomItem customItem = dawnRegistry.getCustomItem(item);
                if (customItem != null) {
                    drops.add(customItem);
                } else {
                    plugin.getLog().info(TextUtil.format("<red>Drop item not found: " + item));
                }
            }
            // Register custom mob
            mob = new CustomMob(parentMob, mobName, MobGroups.valueOf(parentFileName.toUpperCase()));
            mob.setCustomDrops(drops);
            dawnRegistry.registerCustomMob(mob);

            plugin.getLog().info(TextUtil.sendLogInfo("CustomMobLoader", TextUtil.LogInfo.INFO, "Registered custom mob: " + mobName));
        }
    }
}
