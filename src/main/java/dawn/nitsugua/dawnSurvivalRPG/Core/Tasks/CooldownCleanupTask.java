package dawn.nitsugua.dawnSurvivalRPG.Core.Tasks;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.TimeUnit;
import dawn.nitsugua.dawnSurvivalRPG.Utils.ScheduledTask;

public class CooldownCleanupTask {
    private static final DawnRegistry dr = DawnRegistry.getInstance();
    public static void startCooldownCleanup() {
        ScheduledTask task = new ScheduledTask("COOLDOWN_CLEANUP_TASK", CooldownCleanupTask::cleanUpCooldowns)
                .setInterval(5)
                .setTimeUnit(TimeUnit.MINUTE);
        dr.registerTask(task.getTaskName(), task);
    }

    private static void cleanUpCooldowns() {
        long cooldownCutoff = System.currentTimeMillis() - (5 * 60 * 1000); //5 minutes
        dr.removeCooldowns(cooldownCutoff);
    }
}
