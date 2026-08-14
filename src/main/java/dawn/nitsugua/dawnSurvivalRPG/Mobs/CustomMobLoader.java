package dawn.nitsugua.dawnSurvivalRPG.Mobs;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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
        if(file.getName().endsWith(".yml")) {
            ConfigurationSection config = YamlConfiguration.loadConfiguration(file);

            CustomMob mob = null;
        }
    }
}
