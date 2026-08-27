package dawn.nitsugua.dawnSurvivalRPG.Core.Tasks;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.MobManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.TaskManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.TimeUnit;
import dawn.nitsugua.dawnSurvivalRPG.Utils.ScheduledTask;

public class DungeonTasks {
    private static final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    private static ScheduledTask spawnDungeonMobs;
    private static TaskManager taskManager;

    public static void runDungeonMobSpawning(DawnSurvivalRPG plugin) {
        taskManager = plugin.getTaskManager();
        spawnDungeonMobs = new ScheduledTask("SPAWN_DUNGEON_MOBS_TASK", () -> MobManager.spawnDungeonMobs(plugin))
                .setInterval(5)
                .setTimeUnit(TimeUnit.SECOND);
        taskManager.registerTask(spawnDungeonMobs);
    }

    public static void pauseDungeonMobSpawning() {
        taskManager.removeTask(spawnDungeonMobs);
    }
}
