package me.tecnio.antihaxerman.config;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.processor.GhostBlockProcessor;
import me.tecnio.antihaxerman.manager.CheckManager;
import me.tecnio.antihaxerman.util.ColorUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class Config {

    public boolean TESTMODE;

    public String LOG_FORMAT, ALERT_FORMAT, PREFIX, CLIENT_KICK_MESSAGE, GUIPREFIX, VPN_MESSAGE;

    public boolean LOGGING_ENABLED, VPN_ENABLED, API_ENABLED, GHOST_BLOCK_ENABLED, GHOST_BLOCK_LAG_BACK, GLOBALCMD, BANTIMER, CLIENT_ENABLED, CLIENT_CASE_SENSITIVE;

    public GhostBlockProcessor.Mode GHOST_BLOCK_MODE;

    public int GHOST_BLOCK_MAX_PING, BANTIMERTIME;

    public List<String> ENABLED_CHECKS = new ArrayList<>();
    public List<String> SETBACK_CHECKS = new ArrayList<>();
    public Map<String, Integer> MAX_VIOLATIONS = new HashMap<>();
    public Map<String, List<String>> PUNISH_COMMANDS = new HashMap<>();

    public List<String> GLOBAL_COMMANDS, TIMER_COMMANDS, BLOCKED_CLIENTS;

    public void updateConfig() {
        try {
            TESTMODE = getBoolean("testmode");

            LOGGING_ENABLED = getBoolean("logging.enabled");
            LOG_FORMAT = getString("logging.log-format");

            ALERT_FORMAT = getString("violations.alert-format");

            GHOST_BLOCK_ENABLED = getBoolean("ghost-block-handler.enabled");
            GHOST_BLOCK_LAG_BACK = getBoolean("ghost-block-handler.lag-back");
            GHOST_BLOCK_MAX_PING = getInteger("ghost-block-handler.max-ping");
            GHOST_BLOCK_MODE = GhostBlockProcessor.Mode.valueOf(getString("ghost-block-handler.mode"));

            GLOBALCMD = getBoolean("global-punish");
            GLOBAL_COMMANDS = getList("global-punish-commands");

            BANTIMER = getBoolean("punish-timer");
            BANTIMERTIME = getInteger("punish-timer-time");
            TIMER_COMMANDS = getList("punish-timer-commands");

            CLIENT_ENABLED = getBoolean("client.enabled");
            CLIENT_CASE_SENSITIVE = getBoolean("client.case-sensitive");
            BLOCKED_CLIENTS = getList("client.blocked");
            CLIENT_KICK_MESSAGE = getString("client.kick-message");

            PREFIX = getString("response.anticheat.prefix");
            GUIPREFIX = getString("response.gui.prefix");

            VPN_ENABLED = getBoolean("vpn.enabled");
            VPN_MESSAGE = ColorUtil.translate(getString("vpn.message").replaceAll("%nl%", "\n"));

            for (final Class<?> check : CheckManager.CHECKS) {
                final CheckInfo checkInfo = check.getAnnotation(CheckInfo.class);

                String checkType = "";

                if (check.getName().contains("combat")) {
                    checkType = "combat";
                } else if (check.getName().contains("movement")) {
                    checkType = "movement";
                } else if (check.getName().contains("player")) {
                    checkType = "player";
                }

                for (final Field field : check.getDeclaredFields()) {
                    if (field.getType().equals(ConfigValue.class)) {
                        final boolean accessible = field.isAccessible();
                        field.setAccessible(true);

                        final String name = ((ConfigValue) field.get(null)).getName();
                        final ConfigValue value = ((ConfigValue) field.get(null));
                        final ConfigValue.ValueType type = value.getType();

                        switch (type) {
                            case BOOLEAN:
                                value.setValue(getBooleanChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case INTEGER:
                                value.setValue(getIntegerChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case DOUBLE:
                                value.setValue(getDoubleChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case STRING:
                                value.setValue(getStringChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case LONG:
                                value.setValue(getLongChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                        }

                        field.setAccessible(accessible);
                    }
                }

                final boolean enabled = getBooleanChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".enabled");

                final int maxViolations = getIntegerChecks("checks." + checkType.toLowerCase() + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".max-violations");
                final List<String> punishCommand = getListChecks("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".punish-commands");
                if (checkType.equals("movement")) {
                    final boolean setBack = getBooleanChecks("checks.movement." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + ".setback");

                    if (setBack) {
                        SETBACK_CHECKS.add(check.getSimpleName());
                    }
                }

                if (enabled) {
                    ENABLED_CHECKS.add(check.getSimpleName());
                }
                PUNISH_COMMANDS.put(check.getSimpleName(), punishCommand);
                if (maxViolations == 0) {
                    return;
                }
                MAX_VIOLATIONS.put(check.getSimpleName(), maxViolations);
            }
        } catch (final Exception exception) {
            Bukkit.getLogger().severe("Could not properly load config.");
            exception.printStackTrace();
        }

    }

    private boolean getBoolean(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getBoolean(string);
    }

    private boolean getBooleanChecks(final String string) {
        return AntiHaxerman.INSTANCE.getYaml().getBoolean(string);
    }

    public String getString(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getString(string);
    }

    public String getStringChecks(final String string) {
        return AntiHaxerman.INSTANCE.getYaml().getString(string);
    }

    private int getInteger(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getInt(string);
    }

    private int getIntegerChecks(final String string) {
        return AntiHaxerman.INSTANCE.getYaml().getInt(string);
    }

    private double getDouble(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getDouble(string);
    }

    private double getDoubleChecks(final String string) {
        return AntiHaxerman.INSTANCE.getYaml().getDouble(string);
    }

    private long getLong(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getLong(string);
    }

    private long getLongChecks(final String string) {
        return AntiHaxerman.INSTANCE.getYaml().getLong(string);
    }

    private List<String> getList(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getStringList(string);
    }

    private List<String> getListChecks(final String string) {
        return AntiHaxerman.INSTANCE.getYaml().getStringList(string);
    }
}
