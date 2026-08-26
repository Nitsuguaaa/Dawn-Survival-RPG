package dawn.nitsugua.dawnSurvivalRPG.Utils;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class InitialSetup {

    public static void runFirstInitialize(@NotNull DawnSurvivalRPG plugin) {
        File pluginFolder = plugin.getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
            plugin.getLog().info("Created Plugin Folder");
        }

        File itemsFolder = new File(pluginFolder, "items");
        if (!itemsFolder.exists()) {
            itemsFolder.mkdirs();
            plugin.getLogger().info("Created Items Folder");
        }

        File mobFolder = new File(pluginFolder, "mobs");
        if(!mobFolder.exists()) {
            mobFolder.mkdirs();
            plugin.getLogger().info("Created Mob Folder");
        }

        File mobDropFolder = new File(mobFolder, "common");
        if(!mobDropFolder.exists()) {
            mobDropFolder.mkdirs();
            plugin.getLogger().info("Created Mob drops Folder");
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

                yaml.set("custom-material", "END_SWORD");
                yaml.set("rarity", "RARE");
                yaml.set("name", "End Sword");
                yaml.set("material", "DIAMOND_SWORD");
                yaml.set("weapon-damage", 7);
                yaml.set("category", "WEAPON");
                yaml.set("sub-category", "SWORD");
                yaml.set("lore", Arrays.asList(
                   "<dark_gray>Damage:</dark_gray> <red>20",
                   "",
                   "<gold>Ability: Teleport</gold><bold><yellow> RIGHT CLICK</yellow></bold>",
                   "<gray>Teleports</gray> <blue>8</blue> <gray>blocks in front of you</gray>",
                   "<dark_gray>Mana Cost:</dark_gray> <dark_aqua>50</dark_aqua>",
                   "<dark_gray>Cooldown:</dark_gray> <green>1s</green>"
                ));
                
                yaml.set("attributes.intelligence", 20);

                yaml.set("skill.type", "ACTIVE");
                yaml.set("skill.mana-cost", 50.0);
                yaml.set("skill.cooldown", 1.0);

                yaml.save(sampleItem);
                plugin.getLogger().info("Created a sample item: end_sword.yml");

            } catch (IOException e) {
                plugin.getLogger().info("Failed to create sample item: " + e.getMessage());
            }
        }

        File sampleMobDrop = new File(mobDropFolder, "graveyard-zombie.yml");
        if(!sampleMobDrop.exists()) {
            try {
                YamlConfiguration yaml = new YamlConfiguration();

                yaml.set("name", "GRAVEYARD_ZOMBIE");
                yaml.set("parent-mob", "ZOMBIE");
                yaml.set("display-name", "<red>Graveyard Zombie");

                yaml.set("stats.max-health", 20.0);
                yaml.set("stats.attack-damage", 3.0);
                yaml.set("stats.movement-speed", 1.5);
                yaml.set("stats.movement-range", 16.0);
                yaml.set("stats.attack-speed", 4.0);
                //yaml.set("rarity", "RARE");

                yaml.save(sampleMobDrop);
                plugin.getLogger().info("Created a sample item: graveyard-zombie.yml");

            } catch (IOException e) {
                plugin.getLogger().info("Failed to create sample item: " + e.getMessage());
            }
        }

        File defaultMessages = new File(pluginFolder, "messages.yml");
        if(!defaultMessages.exists()) {
            try {
                YamlConfiguration yaml = new YamlConfiguration();

                yaml.set("Error", "An error occurred");
                yaml.save(defaultMessages);
                plugin.getLogger().info("Created a default messages: messages.yml");

            } catch (IOException e) {

                plugin.getLogger().info("Failed to create messages");
            }
        }
    }
}
