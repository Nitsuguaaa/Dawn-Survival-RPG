package dawn.nitsugua.dawnSurvivalRPG.Core.Tasks;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.DungeonManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.MobManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.TaskManager;
import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.TimeUnit;
import dawn.nitsugua.dawnSurvivalRPG.Utils.ScheduledTask;
import dawn.nitsugua.dawnSurvivalRPG.Utils.TextUtil;

public class DungeonTasks {
    private static final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    private static ScheduledTask spawnDungeonMobs;
    private static TaskManager taskManager;

    public static void runDungeonMobSpawning(DawnSurvivalRPG plugin) {
        if (spawnDungeonMobs != null) {
            plugin.getLog().info(TextUtil.sendLogInfo("DungeonTasks", TextUtil.LogInfo.ERROR,"Dungeon spawn task already running!"));
            return;
        }

        taskManager = plugin.getTaskManager();
        spawnDungeonMobs = new ScheduledTask("SPAWN_DUNGEON_MOBS_TASK", () -> DungeonManager.spawnDungeonMobs(plugin, dawnRegistry))
                .setInterval(10)
                .setTimeUnit(TimeUnit.SECOND);
        taskManager.registerTask(spawnDungeonMobs);

        plugin.getLog().info(TextUtil.format("<green>DUNGEON TASKS RUNNING!"));
    }

    public static void pauseDungeonMobSpawning(DawnSurvivalRPG plugin) {
        if(spawnDungeonMobs == null) return;
        taskManager.removeTask(spawnDungeonMobs);
        spawnDungeonMobs = null;
        plugin.getLog().info(TextUtil.format("<red>DUNGEON TASKS STOPPED!"));
    }
}
