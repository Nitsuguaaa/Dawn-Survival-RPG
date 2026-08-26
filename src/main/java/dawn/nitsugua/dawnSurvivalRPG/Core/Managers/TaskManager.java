package dawn.nitsugua.dawnSurvivalRPG.Core.Managers;

import dawn.nitsugua.dawnSurvivalRPG.Core.Registry.DawnRegistry;
import dawn.nitsugua.dawnSurvivalRPG.Utils.GlobalTimer;
import dawn.nitsugua.dawnSurvivalRPG.Utils.ScheduledTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final DawnRegistry dawnRegistry = DawnRegistry.getInstance();
    //Why not just go straight to registry? Because I might add other logic, defeats the purpose of a registry that just handles storage
    public void removeTask(ScheduledTask task) {
        dawnRegistry.removeTask(task.getTaskName());
    }

    public void registerTask(ScheduledTask task) {
        dawnRegistry.registerTask(task.getTaskName(), task);
    }

    public void processTasks(GlobalTimer time) {
        Map<String, ScheduledTask> tasks = dawnRegistry.getAllTasks();
        List<String> toRemove = new ArrayList<>();
        for(ScheduledTask task : tasks.values()) {
            if (shouldRun(task, time)) {
                task.run();

                if(task.shouldRemoveAfterRun() || (task.getMaxRuns()) != -1 && task.getRuns() >= task.getMaxRuns()) {
                    toRemove.add(task.getTaskName());
                }
            }
        }

        for(String name : toRemove) {
            dawnRegistry.removeTask(name);
        }
    }

    public boolean shouldRun(ScheduledTask task, GlobalTimer time) {
        if(task.getMaxRuns() != -1 && task.getRuns() >= task.getMaxRuns()) {
            return false; //assumed task is completed, delete in registry
        }

        int current = switch (task.getTimeUnit()) {
            case TICK -> (int) time.getTick();
            case SECOND -> time.getSecond();
            case MINUTE -> time.getMinute();
            case HOUR -> time.getHour();
        };

        //now check if it already ran on the same point
        if(task.getLastRan() == current) {
            return false; //huh now it's the same as get max runs, I should create an ENUM
        }

        boolean shouldRun = false;

        if(task.getSpecificTimes() != null && !task.getSpecificTimes().isEmpty()) {
            shouldRun = task.getSpecificTimes().contains(current);
        } else if (task.getInterval() > 0) {
            shouldRun = current % task.getInterval() == 0;
        } else {
            shouldRun = true;
        }

        if (shouldRun) {
            task.setLastRan(current);
            return true;
        }

        return false;
    }
}
