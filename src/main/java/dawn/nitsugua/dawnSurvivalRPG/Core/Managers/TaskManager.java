package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.TimeUnit;
import dawn.nitsugua.dawnSurvivalRPG.Utils.GlobalTimer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.ScheduledTask;

import java.util.HashMap;

public class TaskManager {
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();

    //Why not just go straight to registry? Because I might add other logic, defeats the purpose of a registry that just handles storage
    public void registerTask(String name, Runnable runnable, int interval, TimeUnit unit) {
        ScheduledTask task = new ScheduledTask(name, runnable)
                .setInterval(interval)
                .setTimeUnit(unit);
        dawnRegistry.registerTask(name, task);
    }

    public void removeTask(String name) {
        dawnRegistry.removeTask(name);
    }

    public void processTasks(GlobalTimer time) {
        HashMap<String, ScheduledTask> tasks = dawnRegistry.getAllTasks();
        for(ScheduledTask task : tasks.values()) {
            if (shouldRun(task, time)) {
                task.run();
            }
        }
    }

    public boolean shouldRun(ScheduledTask task, GlobalTimer time) {
        if(task.getMaxRuns() != -1 && task.getRuns() >= task.getMaxRuns()) {
            return false; //assumed task is completed, delete in registry
        }

        int current = switch (task.getTimeUnit()) {
            case SECOND -> time.getSecond();
            case MINUTE -> time.getMinute();
            case HOUR -> time.getHour();
        };

        if(task.getSpecificTimes() != null && !task.getSpecificTimes().isEmpty()) {
            return task.getSpecificTimes().contains(current);
        }

        if (task.getInterval() > 0) {
            return current % task.getInterval() == 0;
        }
        return true;
    }
}
