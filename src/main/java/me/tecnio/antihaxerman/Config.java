package me.tecnio.antihaxerman;

public final class Config {

    public static int MAX_CPS;

    public static boolean ENABLE_ALERTS_ON_JOIN;

    public static boolean LOG_TO_CONSOLE, ENABLE_LOGGING;

    public static void updateConfig(){
        MAX_CPS = AntiHaxerman.getInstance().getConfig().getInt("check-settings.max-cps");

        ENABLE_ALERTS_ON_JOIN = AntiHaxerman.getInstance().getConfig().getBoolean("enable-alerts-on-join");

        LOG_TO_CONSOLE = AntiHaxerman.getInstance().getConfig().getBoolean("logging.log-to-console");
        ENABLE_LOGGING = AntiHaxerman.getInstance().getConfig().getBoolean("logging.enabled");
    }
}
