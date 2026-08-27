package dawn.nitsugua.dawnSurvivalRPG.Utils;

import dawn.nitsugua.dawnSurvivalRPG.Core.Managers.TaskManager;
import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;
import org.bukkit.Bukkit;

public class GlobalTicker implements Runnable {

    private DawnSurvivalRPG plugin;
    private final GlobalTimer timer;
    private final TaskManager taskManager;
    private final int TICK_INTERVAL = 10;
    private int globalTicker;

    public GlobalTicker(DawnSurvivalRPG plugin, TaskManager taskManager) {
        this.plugin = plugin;
        this.timer = new GlobalTimer(plugin);
        this.timer.syncToSystem();
        this.taskManager = taskManager;
    }

    public void start() {
        globalTicker = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin, this, 0L, TICK_INTERVAL
        );
        plugin.getLog().info(TextUtil.sendLogInfo("GlobalTicker", TextUtil.LogInfo.SUCCESS, "Global Ticker has started!"));
    }

    public void stop() {
        if(globalTicker != -1) {
            Bukkit.getScheduler().cancelTask(globalTicker);
            globalTicker = -1;
            plugin.getLog().info(TextUtil.sendLogInfo("GlobalTicker", TextUtil.LogInfo.SUCCESS, "Global Ticker has been stopped!"));
        }
    }

    @Override
    public void run() {
        timer.tick();

        // ✅ Debug: Check if tick() is being called
        //plugin.getLog().info("[GlobalTicker] Tick: " + timer.getTick());

        taskManager.processTasks(timer);
    }

    public GlobalTimer getTimer() {
        return timer;
    }
}
