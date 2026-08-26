package dawn.nitsugua.dawnSurvivalRPG.Mobs;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.MobGroups;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

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

            String mobName = config.getString("name");
            String parentMobName = config.getString("parent-mob");
            String displayNameStr = config.getString("display-name", "<white>" + mobName);

            if(mobName == null || parentMobName == null) {
                plugin.getLog().info(TextUtil.sendLogInfo("CustomMobLoader", TextUtil.LogInfo.ERROR, "Missing required fields in: <white>" + file.getName()));
            }

            EntityType parentType;
            try {
                parentType = EntityType.valueOf(parentMobName.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLog().info(TextUtil.sendLogInfo("CustomMobLoader", TextUtil.LogInfo.ERROR, "Invalid parent mob in: <white>" + file.getName()));
                return;
            }

            MobGroups group;
            try {
                group = MobGroups.valueOf(parentFileName.toUpperCase());
            } catch (IllegalArgumentException e) {
                group = MobGroups.COMMON; // Fallback
            }


            ConfigurationSection stats = config.getConfigurationSection("stats");
            double maxHealth = stats != null ? stats.getDouble("max-health", 20.0) : 20.0;
            double attackDamage = stats != null ? stats.getDouble("attack-damage", 3.0) : 3.0;
            double movementSpeed = stats != null ? stats.getDouble("movement-speed", 0.23) : 0.23;
            double followRange = stats != null ? stats.getDouble("follow-range", 16.0) : 16.0;
            double attackSpeed = stats != null ? stats.getDouble("attack-speed", 4.0) : 4.0;

            Component displayName = TextUtil.format(displayNameStr);

            CustomMob mob = new CustomMob(
              mobName, parentType, group, displayName, maxHealth, attackDamage, movementSpeed, followRange, attackSpeed
            );

            dawnRegistry.registerCustomMob(mob);
            plugin.getLog().info(TextUtil.sendLogInfo("CustomMobLoader", TextUtil.LogInfo.INFO, "Registered custom mob: <white>" + mobName));
        }
    }
}
