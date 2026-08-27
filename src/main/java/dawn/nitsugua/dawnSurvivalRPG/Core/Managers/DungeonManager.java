package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Dungeons.SpawnArea;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.CustomMob;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class DungeonManager {
    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    private final File pluginFolder;
    private File dungeonFolder;
    private File dungeons;

    public DungeonManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.pluginFolder = plugin.getDataFolder();
        initializeDungeons();
    }

    public void createDungeon(Player player, String name, String mobName, int maxSpawn) {
        CustomMob blueprint = dawnRegistry.getCustomMob(mobName.toUpperCase());

        for (SpawnArea existingArea : dawnRegistry.getAllSpawners().values()) {
            if (existingArea.getSpawnerName().equalsIgnoreCase(name)) {
                player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "A spawner named <white>" + name + "<red> already exists!"));
                return;
            }
        }

        if(blueprint == null) {
            plugin.getLog().info(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Dawn mob not found: <white>" + mobName));
            return;
        }

        UUID uuid = UUID.randomUUID();
        SpawnArea spawnArea = new SpawnArea(uuid, name, blueprint, maxSpawn, player.getLocation(), player.getWorld());
        dawnRegistry.registerDawnSpawner(spawnArea);
        saveSpawner(spawnArea);



        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Successfully added a dawn spawner: <white>" + name));
    }

    public void addNode(Player player, String spawnerName) {
        SpawnArea area = getSpawnerByName(spawnerName);
        if(area == null) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "A spawner named <white>" + spawnerName+ "<red> not found!"));
            return;
        }

        area.setNode(player.getLocation());
        saveNode(area);
        player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Node added for <white>" + spawnerName + "! Total nodes: " + area.getNodes().size()));
    }

    public void initializeDungeons() {
        plugin.getLog().info(TextUtil.sendLogInfo("DungeonManager", TextUtil.LogInfo.INFO, "Initializing Dungeons..."));

        dungeonFolder = new File(pluginFolder, "dungeons");
        if(!dungeonFolder.exists()) {
            dungeonFolder.mkdirs();
            plugin.getLog().info(TextUtil.sendLogInfo("DungeonManager", TextUtil.LogInfo.INFO, "Dungeon Folder created!..."));
        }

        dungeons = new File(dungeonFolder, "dungeons.yml");
        if(!dungeons.exists()) {
            try {
                YamlConfiguration yaml = new YamlConfiguration();
                yaml.save(dungeons);
                plugin.getLog().info(TextUtil.sendLogInfo("DungeonManager", TextUtil.LogInfo.INFO, "Dungeon YAML created!..."));
            } catch (IOException e) {
                plugin.getLogger().info("Failed to create Dungeon YAML: " + e.getMessage());
            }
        }
    }

    public void saveDungeons() {

    }

    public void saveSpawner(SpawnArea area) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dungeons);
        String basePath = area.getUuid().toString();

        // 2. Inject the data
        yaml.set(basePath + ".name", area.getSpawnerName());
        yaml.set(basePath + ".dawn-mob", area.getCustomMob().getMobName());
        yaml.set(basePath + ".max-spawn", area.getMaxSpawns());

        // Manual coordinate saving is the safest way to prevent world-loading bugs on startup
        Location center = area.getCenter();
        yaml.set(basePath + ".world", center.getWorld().getName());
        yaml.set(basePath + ".center-x", center.getX());
        yaml.set(basePath + ".center-y", center.getY());
        yaml.set(basePath + ".center-z", center.getZ());
    }

    public void saveNode(SpawnArea area) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dungeons);
        String basePath = area.getUuid().toString();

        for(Map.Entry<UUID, Location> entry : area.getNodes().entrySet()) {
            String nodePath = basePath + ".nodes." + entry.getKey().toString();
            Location loc = entry.getValue();

            yaml.set(nodePath + ".x", loc.getX());
            yaml.set(nodePath + ".y", loc.getY());
            yaml.set(nodePath + ".z", loc.getZ());
        }
    }

    public SpawnArea getSpawnerByName(String name) {
        for (SpawnArea area : dawnRegistry.getAllSpawners().values()) {
            if (area.getSpawnerName().equalsIgnoreCase(name)) {
                return area;
            }
        }
        return null; // Not found
    }
}
