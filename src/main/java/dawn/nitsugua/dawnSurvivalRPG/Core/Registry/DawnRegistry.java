package dawn.nitsugua.dawnSurvivalRPG.Core.Registry;

import dawn.nitsugua.dawnSurvivalRPG.Core.Skills.Skill;
import dawn.nitsugua.dawnSurvivalRPG.Dungeons.SpawnArea;
import dawn.nitsugua.dawnSurvivalRPG.Items.CustomItem;
import dawn.nitsugua.dawnSurvivalRPG.Mobs.CustomMob;
import dawn.nitsugua.dawnSurvivalRPG.Player.DawnPlayer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.ScheduledTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DawnRegistry {
    private static final DawnRegistry INSTANCE = new DawnRegistry();
    private final ConcurrentHashMap<String, CustomItem> customItems = new ConcurrentHashMap<>(); //Custom Items
    private final ConcurrentHashMap<String, Skill> skills = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, DawnPlayer> playerData = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CustomMob> customMobs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, SpawnArea> dawnSpawners = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ScheduledTask> tasks = new ConcurrentHashMap<>();

    private DawnRegistry() {}
    public static DawnRegistry getInstance() {return INSTANCE;};

    public void registerItem(CustomItem customItem) {
        customItems.put(customItem.getCustomMaterial(), customItem);
    }
    @Nullable
    public CustomItem getCustomItem(String customMaterial) {
        return customItems.get(customMaterial);
    }

    public void registerSkill(Skill skill) {
        skills.put(skill.getId(), skill);
    }
    @Nullable
    public Skill getSkill(String skill) { return skills.get(skill);}

    public void registerPlayer(UUID playerId, DawnPlayer player) {
        playerData.put(playerId, player);
    }
    @Nullable
    public DawnPlayer getDawnPlayer(UUID player) { return playerData.get(player);}
    public void removePlayer(UUID player) {playerData.remove(player);}
    public Map<UUID, DawnPlayer> getAllPlayers() {return Collections.unmodifiableMap(playerData);}

    public void registerTask(String name, ScheduledTask task) { tasks.put(name, task);}
    @Nullable
    public ScheduledTask getTask(String name) { return tasks.get(name);}
    public void removeTask(String name) { tasks.remove(name);}
    public Map<String, ScheduledTask> getAllTasks() {return Collections.unmodifiableMap(tasks);}

    public void registerCustomMob(CustomMob customMob) {customMobs.put(customMob.getMobName(), customMob);}
    @Nullable
    public CustomMob getCustomMob(String customMob) {return customMobs.get(customMob);}

    public void registerDawnSpawner(SpawnArea spawnArea) {dawnSpawners.put(spawnArea.getUuid(), spawnArea);}
    @Nullable
    public SpawnArea getSpawner(UUID uuid) {return dawnSpawners.get(uuid);}
    public Map<UUID, SpawnArea> getAllSpawners() {return Collections.unmodifiableMap(dawnSpawners);}
}
