package dawn.nitsugua.dawnSurvivalRPG.Items;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CustomItemLoader {
    private final DawnSurvivalRPG plugin;

    public CustomItemLoader(@NotNull DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    //Do I just need separation of methods (static) or I want to use it elsewhere? (instance)
    public static void initialize(@NotNull DawnSurvivalRPG plugin) {
        File customItemFolder = new File(plugin.getDataFolder().getPath() + "/items");
        File[] files = customItemFolder.listFiles();

        if (files == null) { plugin.getLogger().info("An error occurred while parsing the YAML files of custom items"); return;}

        plugin.getLogger().info("Here are the files in the folder: ");
        for (File file : files) {
            File[] subFiles = file.listFiles();

            if (subFiles == null) {plugin.getLogger().info("There seems to be no items in this directory"); return;}
            for (File subFile : subFiles) {
                parseFile(subFile, plugin);
            }

            plugin.getLogger().info(file.getName());
        }
    }

    private static void parseFile(File file, DawnSurvivalRPG plugin) {
        if (file.getName().endsWith(".yml")) {
            plugin.getLogger().info("Yup, this is a YAML file");

            ConfigurationSection config = YamlConfiguration.loadConfiguration(file);

            plugin.getLogger().info("This YAML's name is: " + config.getString("name"));
        }
    }


    public static void reloadCustomItem() {
        //run something
        //has to check if the item it scanned already exists then just update it
    }
}
