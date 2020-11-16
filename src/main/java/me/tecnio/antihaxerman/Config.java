package me.tecnio.antihaxerman;

import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.check.CheckManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class Config {

    public static String ALERT_FORMAT;

    public static boolean LOG_TO_CONSOLE;

    public static Map<String, Boolean> ENABLED_CHECKS = new HashMap<>();
    public static Map<String, List<String>> PUNISH_COMMANDS = new HashMap<>();

    public static void updateSettings() {
        try {
            ALERT_FORMAT = AntiHaxerman.getInstance().getConfig().getString("alerts.format");

            LOG_TO_CONSOLE = AntiHaxerman.getInstance().getConfig().getBoolean("alerts.log-to-console");

            for (final Class<?> clazz : CheckManager.getCHECKS()) {
                 if (clazz.isAnnotationPresent(CheckInfo.class)) {
                     final CheckInfo checkInfo = clazz.getAnnotation(CheckInfo.class);

                     final boolean enabled = AntiHaxerman.getInstance().getConfig().getBoolean("checks." + checkInfo.name().toLowerCase() + ".enabled");
                     final List<String> punishments = AntiHaxerman.getInstance().getConfig().getStringList("checks." + checkInfo.name().toLowerCase() + ".punish-commands");

                    ENABLED_CHECKS.put(checkInfo.name(), enabled);
                    PUNISH_COMMANDS.put(checkInfo.name(), punishments);
                 }
            }
        } catch (Exception exception) {
            AntiHaxerman.getInstance().getLogger().log(Level.SEVERE, "Unable to load the config for AntiHaxerman!\nRestarting the server is advised!\nIf the issue persists reset the config file.");
        }
    }
}
