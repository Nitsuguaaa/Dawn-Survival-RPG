package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class OverworldManager {
    private static YamlConfiguration yamlConfig;
    private static File configFile;

    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    public OverworldManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
    }

    public static void loadStructureYRanges(DawnSurvivalRPG plugin) {
        DawnRegistry.getInstance().clearStructureYRanges();

         configFile = new File(plugin.getDataFolder(), "structure_config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("structure_config.yml", false);
        }
        yamlConfig = YamlConfiguration.loadConfiguration(configFile);

        for(String key : yamlConfig.getKeys(false)) {
            int minY = yamlConfig.getInt(key + ".min-y");
            int maxY = yamlConfig.getInt(key + ".max-y");
            DawnRegistry.getInstance().setStructureYRange(key, new int[]{minY, maxY});
            plugin.getLogger().info("[Config] Loaded Y-Range for " + key + ": " + minY + " to " + maxY);
        }
    }

    public static void updateConfig(DawnSurvivalRPG plugin, String key, int[] range) {
        // 1. Surgically update the specific paths
        yamlConfig.set(key + ".min-y", range[0]);
        yamlConfig.set(key + ".max-y", range[1]);

        // 2. Save the changes to the disk file!
        try {
            yamlConfig.save(configFile);
            plugin.getLogger().info("[Config] Saved " + key + " to disk.");
        } catch (IOException e) {
            plugin.getLogger().severe("[Config] Failed to save structure config: " + e.getMessage());
        }
    }

    public void trackBlock(Block block, Material material, Long duration) {
        dawnRegistry.registerTemporaryBlock(block.getLocation(), System.currentTimeMillis() + duration);
        dawnRegistry.registerExpectedType(block.getLocation(), material);
    }

    public void destroyBlock(Location loc) {
        Block block = loc.getBlock();
        Material expectedType = dawnRegistry.getExpectedTypes().get(loc);

        if(block.getBlockData() instanceof Waterlogged waterlogged) {
            plugin.getLog().info("Hey, this is waterlogged");
            waterlogged.setWaterlogged(false);
            block.setBlockData(waterlogged);
        }

        if(expectedType != null && block.getType() == expectedType) {
            block.setType(Material.AIR);
        }

        dawnRegistry.removeTemporaryBlock(loc);
        dawnRegistry.removeExpectedType(loc);
    }
}
