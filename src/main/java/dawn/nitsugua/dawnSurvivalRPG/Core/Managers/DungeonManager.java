package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.Core.Tasks.DungeonTasks;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Dungeons.SpawnArea;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.CustomMob;
import dawn.nitsugua.dawnSurvivalRPG.Utils.DawnKeys;
import dawn.nitsugua.dawnSurvivalRPG.Utils.MessagesUtil;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DungeonManager {
    private final DawnSurvivalRPG plugin;
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    //private final TaskManager taskManager;

    private final File pluginFolder;
    private File dungeonFolder;
    private File dungeons;

    public DungeonManager(DawnSurvivalRPG plugin) {
        this.plugin = plugin;
        this.pluginFolder = plugin.getDataFolder();
        //this.taskManager = plugin.getTaskManager();
        initializeDungeons();
        loadDungeons();
        dawnRegistry.registerDungeonWorld("dungeon-1");
    }

    public boolean isDungeonWorld(String worldName) {
        return dawnRegistry.getDungeonWorlds().containsKey(worldName);
    }

    public void addPlayerToWorld(String worldName) {
        int count = dawnRegistry.getDungeonWorlds().get(worldName);
        count++;
        dawnRegistry.getDungeonWorlds().put(worldName, count);

        if(count == 1) {
            DungeonTasks.runDungeonMobSpawning(plugin);
        }
    }

    public void removePlayerFromWorld(String worldName) {
        int count = dawnRegistry.getDungeonWorlds().get(worldName);
        count --;

        if(count < 0) count = 0;
        dawnRegistry.getDungeonWorlds().put(worldName, count);

        if(count == 0) {
            DungeonTasks.pauseDungeonMobSpawning(plugin);
        }
    }

    public void createDungeon(Player player, String name, String blueprint, int maxSpawn) {
        //CustomMob blueprint = dawnRegistry.getCustomMob(mobName.toUpperCase());

        for (SpawnArea existingArea : dawnRegistry.getAllSpawners().values()) {
            if (existingArea.getSpawnerName().equalsIgnoreCase(name)) {
                player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "A spawner named <white>" + name + "<red> already exists!"));
                return;
            }
        }

        if(!dawnRegistry.getAllMobs().containsKey(blueprint)) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Dawn mob not found: <white>" + blueprint));
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

        area.addNode(UUID.randomUUID(), player.getLocation());
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

    public void loadDungeons() {
        if(!dungeons.exists()) return;

        plugin.getLog().info(TextUtil.sendLogInfo("DungeonManager", TextUtil.LogInfo.INFO, "Loading Dungeons from YAML..."));
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dungeons);

        for(String uuidString : yaml.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                String name = yaml.getString(uuidString + ".name");
                String blueprint = yaml.getString(uuidString + ".dawn-mob");
                int maxSpawn = yaml.getInt(uuidString + ".max-spawn");
                boolean staged = yaml.getBoolean(uuidString + ".staged");

                String worldName = yaml.getString(uuidString + ".world");
                double x = yaml.getDouble(uuidString + ".center-x");
                double y = yaml.getDouble(uuidString + ".center-y");
                double z = yaml.getDouble(uuidString + ".center-z");

                World world = plugin.getServer().getWorld(worldName);
                if(world == null) {
                    plugin.getLog().info(TextUtil.sendLogInfo("DungeonManager", TextUtil.LogInfo.ERROR,"World not found: " + worldName));
                    continue;
                }

                Location center = new Location(world, x, y, z);
                if(!dawnRegistry.getAllMobs().containsKey(blueprint)) {
                    plugin.getLog().info(TextUtil.sendLogInfo("DungeonManager", TextUtil.LogInfo.ERROR,"Mob not found: " + blueprint));
                    continue;
                }

                SpawnArea area = new SpawnArea(uuid, name, blueprint, maxSpawn, center, world);
                area.setStaged(staged);

                ConfigurationSection nodesSection = yaml.getConfigurationSection(uuidString + ".nodes");
                if (nodesSection != null) {
                    for (String nodeUuidString : nodesSection.getKeys(false)) {
                        try {
                            UUID nodeUuid = UUID.fromString(nodeUuidString);
                            double nx = nodesSection.getDouble(nodeUuidString + ".x");
                            double ny = nodesSection.getDouble(nodeUuidString + ".y");
                            double nz = nodesSection.getDouble(nodeUuidString + ".z");

                            Location nodeLoc = new Location(world, nx, ny, nz);
                            area.addNode(nodeUuid, nodeLoc); // Uses the new method!
                        } catch (Exception e) {
                            plugin.getLog().info(TextUtil.sendLogInfo("DungeonManager", TextUtil.LogInfo.ERROR ,"Failed to load node " + nodeUuidString + " for spawner " + name));
                        }
                    }
                }

                dawnRegistry.registerDawnSpawner(area);

            } catch (Exception e) {
                plugin.getLog().info(TextUtil.sendLogInfo("DungeonManager", TextUtil.LogInfo.ERROR,"Failed to load spawner " + uuidString + ": " + e.getMessage()));
            }
        }

        plugin.getLog().info(TextUtil.sendLogInfo("DungeonManager", TextUtil.LogInfo.SUCCESS, "Dungeons loaded into cache!"));
    }

    public void reload() {
        boolean playersAreInside = false;

        for(int count : dawnRegistry.getDungeonWorlds().values()) {
            if(count > 0) {
                playersAreInside = true;
                break;
            }
        }

        DungeonTasks.pauseDungeonMobSpawning(plugin);
        dawnRegistry.clearDawnSpawners();
        loadDungeons();

        if(playersAreInside) DungeonTasks.runDungeonMobSpawning(plugin);

    }

    public void saveSpawner(SpawnArea area) {
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dungeons);
            String basePath = area.getUuid().toString();

            // 2. Inject the data
            yaml.set(basePath + ".name", area.getSpawnerName());
            yaml.set(basePath + ".dawn-mob", area.getCustomMob());
            yaml.set(basePath + ".max-spawn", area.getMaxSpawns());
            yaml.set(basePath + ".staged", area.isStaged());

            // Manual coordinate saving is the safest way to prevent world-loading bugs on startup
            Location center = area.getCenter();
            yaml.set(basePath + ".world", center.getWorld().getName());
            yaml.set(basePath + ".center-x", center.getX());
            yaml.set(basePath + ".center-y", center.getY());
            yaml.set(basePath + ".center-z", center.getZ());

            yaml.save(dungeons);
        } catch (IOException e) {
            plugin.getLogger().info("Failed to create Dungeon YAML: " + e.getMessage());
        }
    }

    public void saveNode(SpawnArea area) {
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(dungeons);
            String basePath = area.getUuid().toString();

            for (Map.Entry<UUID, Location> entry : area.getNodes().entrySet()) {
                String nodePath = basePath + ".nodes." + entry.getKey().toString();
                Location loc = entry.getValue();

                yaml.set(nodePath + ".x", loc.getX());
                yaml.set(nodePath + ".y", loc.getY());
                yaml.set(nodePath + ".z", loc.getZ());
            }
            yaml.save(dungeons);
        } catch (IOException e) {
        plugin.getLogger().info("Failed to create Dungeon YAML: " + e.getMessage());
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

    public void toggleStage(Player player, String spawnerName) {
        SpawnArea area = getSpawnerByName(spawnerName);
        if (area == null) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Spawner not found: <white>" + spawnerName));
            return;
        }

        // Check if it has any nodes before staging!
        if (!area.isStaged() && area.getNodes().isEmpty()) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.FAILURE, "Cannot stage <white>" + spawnerName + "<red>! It has 0 nodes. Add nodes first."));
            return;
        }

        // Toggle!
        area.setStaged(!area.isStaged());
        saveSpawner(area);

        if (area.isStaged()) {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Spawner <white>" + spawnerName + " <green>is now ACTIVE! Mobs will begin spawning."));
        } else {
            player.sendMessage(MessagesUtil.sendInfo(MessagesUtil.commonMessages.SUCCESS, "Spawner <white>" + spawnerName + " <yellow>is now INACTIVE. Mobs will stop spawning."));
        }
    }

    public static void spawnDungeonMobs(DawnSurvivalRPG plugin, DawnRegistry dawnRegistry) {
        MobManager mobManager = plugin.getMobManager();
        if(mobManager == null) return;

        for(SpawnArea area : dawnRegistry.getAllSpawners().values()) {
            if(!area.isStaged()) continue;
            if(area.getNodes().isEmpty()) continue;

            Location center = area.getCenter();

            if(center.getNearbyPlayers(50.0).isEmpty()) {
                //plugin.getLog().info(TextUtil.format("<red>No nearby players found in: <white>" + area.getSpawnerName())); //DEBUG
                continue;
            }

            Set<UUID> occupiedNodes = new HashSet<>();
            for(Entity entity : center.getNearbyEntities(50.0, 50.0, 50.0)) {
                if(!(entity instanceof Mob)) continue;

                PersistentDataContainer pdc = entity.getPersistentDataContainer();
                String spawnerID = pdc.get(DawnKeys.SPAWNER_UUID, PersistentDataType.STRING);

                if(spawnerID != null && spawnerID.equals(area.getUuid().toString())) {
                    String nodeIdStr = pdc.get(DawnKeys.NODE_ID_STRING, PersistentDataType.STRING);
                    if(nodeIdStr != null) {
                        try {
                            //plugin.getLog().info(TextUtil.format("<red>Node already occupied: <white>" + area.getSpawnerName()));
                            occupiedNodes.add(UUID.fromString(nodeIdStr));
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
            }

            int spawnedThisTick = 0;
            for(Map.Entry<UUID, Location> nodeEntry : area.getNodes().entrySet()) {
                if(spawnedThisTick >= area.getMaxSpawns()) break;

                UUID nodeId = nodeEntry.getKey();
                Location nodeLoc = nodeEntry.getValue();

                if(!occupiedNodes.contains(nodeId)) {
                    Mob mob = mobManager.spawnCustomMob(nodeLoc, area.getCustomMob());

                    if(mob != null) {
                        //plugin.getLog().info(TextUtil.format("<green>Spawning mob in: <white>" + nodeId)); //DEBUG
                        PersistentDataContainer pdc = mob.getPersistentDataContainer();
                        pdc.set(DawnKeys.SPAWNER_UUID, PersistentDataType.STRING, area.getUuid().toString());
                        pdc.set(DawnKeys.NODE_ID_STRING, PersistentDataType.STRING, nodeId.toString());

                        spawnedThisTick++;
                    }
                }
            }


        }
    }
}
