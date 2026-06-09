package dawn.nitsugua.dawnSurvivalRPG.Utils;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class InitialSetup {
    private final DawnSurvivalRPG plugin;

    public InitialSetup(@NotNull DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public void runFirstInitialize() {
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
            plugin.getLogger().info("Created Plugin Folder");
        }

        File itemsFolder = new File(pluginFolder, "items");
        if (!itemsFolder.exists()) {
            itemsFolder.mkdirs();
            plugin.getLogger().info("Created Items Folder");
        }

        File weaponsFolder = new File(itemsFolder, "weapons");
        if (!weaponsFolder.exists()) {
            weaponsFolder.mkdirs();
            plugin.getLogger().info("Created Weapons Folder");
        }

        File sampleItem = new File(weaponsFolder, "end_sword.yml");
        if(!sampleItem.exists()) {
            try {
                YamlConfiguration yaml = new YamlConfiguration();

                yaml.set("name", "End Sword");
                yaml.set("damage", 20);
                yaml.set("material", "DIAMOND_SWORD");

                yaml.save(sampleItem);
                plugin.getLogger().info("Created a sample item: end_sword.yml");

            } catch (Exception e) {
                plugin.getLogger().info("Failed to create sample item: " + e.getMessage());
            }
        }
    }
}
