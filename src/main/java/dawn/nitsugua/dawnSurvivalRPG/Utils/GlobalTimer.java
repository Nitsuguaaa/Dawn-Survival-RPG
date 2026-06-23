package dawn.nitsugua.dawnSurvivalRPG.Utils;

import dawn.nitsugua.dawnSurvivalRPG.DawnSurvivalRPG;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class GlobalTimer {

    private int second = 0;
    private int minute = 0;
    private int hour = 0;

    private int dayOfTheMonth; //used for monthly resets
    private DayOfWeek dayOfTheWeek; //use for weekly resets and weekend bonuses

    private long tick = 0;

    private static final ZoneId TIMEZONE = ZoneId.of("Asia/Manila");

    public GlobalTimer(DawnSurvivalRPG plugin) {
        syncToSystem();

        plugin.getLog().info(TextUtil.sendLogInfo("GlobalTimer", TextUtil.LogInfo.SUCCESS, "Global Timer Initialized!"));
        plugin.getLog().info(TextUtil.sendLogInfo("GlobalTimer", TextUtil.LogInfo.SUCCESS, "Hour: " + hour + ", Minute: " + minute + ", Second: " + second + ", Day of the month: " + dayOfTheMonth + ", Day of the week: " + dayOfTheWeek));
    }

    public void syncToSystem() {
        ZonedDateTime now = ZonedDateTime.now(TIMEZONE);
        this.hour = now.getHour();
        this.minute = now.getMinute();
        this.second = now.getSecond();

        this.dayOfTheMonth = now.getDayOfMonth();
        this.dayOfTheWeek = now.getDayOfWeek();
    }

    public void tick() {
        tick ++;

        if (tick % 20 == 0) {
            second++;
            if (second >= 60) {
                second = 0;
                minute++;
                if (minute >= 60) {
                    minute = 0;
                    hour++;
                    if (hour >= 24) {
                        hour = 0;
                    }
                }
            }
        }
    }

    public DayOfWeek getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public int getDayOfTheMonth() {
        return dayOfTheMonth;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public long getTick() {
        return tick;
    }
}
