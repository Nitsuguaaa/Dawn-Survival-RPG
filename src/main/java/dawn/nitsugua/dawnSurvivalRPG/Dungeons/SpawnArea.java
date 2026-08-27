package dawn.nitsugua.dawnSurvivalRPG.Dungeons;

import dawn.nitsugua.dawnSurvivalRPG.Mobs.CustomMob;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class SpawnArea {

    private UUID uuid;
    private String name;
    private CustomMob customMob;
    private int maxSpawn;

    private Location center;
    private World world;
    private HashMap<UUID, Location> nodes;

    public SpawnArea(UUID uuid, String name, CustomMob customMob, int maxSpawn, Location center, World world) {
        this.uuid = uuid;
        this.name = name;
        this.customMob = customMob;
        this.maxSpawn = maxSpawn;
        this.center = center;
        this.world = world;
        this.nodes = new HashMap<>();
    }

    public UUID getUuid() {return uuid;}
    public String getSpawnerName() {return name;}
    public CustomMob getCustomMob() {return customMob;}
    public int getMaxSpawns() {return maxSpawn;}
    public Location getCenter() {return center;}
    public World getWorld() {return world;}
    public Map<UUID, Location> getNodes() {return Collections.unmodifiableMap(nodes);}

    public void addNode(UUID uuid, Location location) {
        nodes.put(uuid, location);
    }
}
