package dawn.nitsugua.dawnSurvivalRPG.Utils;

import dawn.nitsugua.dawnSurvivalRPG.Utils.Enums.TimeUnit;

import java.util.HashSet;
import java.util.Set;

public class ScheduledTask {

    private final String name;
    private final Runnable runnable;
    private int interval;
    private TimeUnit timeUnit;

    private int maxRuns = -1;
    private int runs = 0;

    private int lastRan = -1;

    private Set<Integer> specificTimes = null;
    private boolean removeAfterRun = false;

    public ScheduledTask(String name, Runnable runnable) {
        this.name = name;
        this.runnable = runnable;
    }

    public ScheduledTask setInterval(int interval) {
        this.interval = interval;
        return this;
    }

    public ScheduledTask setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public ScheduledTask removeTaskAfterRun(boolean option) {
        this.removeAfterRun = option;
        return this;
    }

    public ScheduledTask setSpecificTimes(int... times) {
        this.specificTimes = new HashSet<>();
        for (int time : times) {
            specificTimes.add(time);
        }
        return this;
    }

    public ScheduledTask setMaxRuns(int maxRuns) {
        this.maxRuns = maxRuns;
        return this;
    }

    public void setLastRan(int last) {
        this.lastRan = last;
    }

    public void run() {
        if(runnable != null) {
            runnable.run();
            runs++;
        }
    }

    public int getLastRan() {return lastRan;}
    public int getMaxRuns() {return maxRuns;}
    public int getRuns() {return runs;}
    public TimeUnit getTimeUnit() {return timeUnit;}
    public boolean shouldRemoveAfterRun() {return removeAfterRun;}
    public String getTaskName() {return name;}
    public Set<Integer> getSpecificTimes() {return specificTimes;}
    public int getInterval() {return interval;}
}
