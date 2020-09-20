package me.tecnio.antihaxerman;

public final class Config {

    public static int MAX_CPS;
    public static double MAX_REACH, TIMER_DEVIATION, TIMER_DEVIATION_DIFF, MAX_TIMER, MIN_TIMER;
    public static float CLIMB_SPEED;

    public static boolean ENABLE_ALERTS_ON_JOIN;

    public static boolean LOG_TO_CONSOLE, ENABLE_LOGGING;

    public static void updateConfig(){
        MAX_CPS = AntiHaxerman.getInstance().getConfig().getInt("check-settings.max-cps");
        MAX_REACH = AntiHaxerman.getInstance().getConfig().getDouble("check-settings.max-reach");
        TIMER_DEVIATION = AntiHaxerman.getInstance().getConfig().getDouble("check-settings.lowest-deviation");
        TIMER_DEVIATION_DIFF = AntiHaxerman.getInstance().getConfig().getDouble("check-settings.deviation-diff-limit");
        MAX_TIMER = AntiHaxerman.getInstance().getConfig().getDouble("check-settings.max-timer");
        MIN_TIMER = AntiHaxerman.getInstance().getConfig().getDouble("check-settings.min-timer");
        CLIMB_SPEED = (float) AntiHaxerman.getInstance().getConfig().getDouble("check-settings.climb-speed");


        ENABLE_ALERTS_ON_JOIN = AntiHaxerman.getInstance().getConfig().getBoolean("enable-alerts-on-join");

        LOG_TO_CONSOLE = AntiHaxerman.getInstance().getConfig().getBoolean("logging.log-to-console");
        ENABLE_LOGGING = AntiHaxerman.getInstance().getConfig().getBoolean("logging.enabled");
    }
}
