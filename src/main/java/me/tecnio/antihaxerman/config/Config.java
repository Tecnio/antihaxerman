/*
 *  Copyright (C) 2020 - 2021 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.config;

import lombok.experimental.UtilityClass;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.manager.CheckManager;
import me.tecnio.antihaxerman.util.ColorUtil;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class Config {

    public boolean TESTMODE;
    public String PREFIX, NO_PERMS, COMMAND_PREFIX, ALERT_FORMAT, COMMAND_NAME, LOG_FORMAT, CLIENT_KICK_MESSAGE;
    public int VL_TO_ALERT, CLEAR_VIOLATIONS_DELAY;
    public boolean BYPASS_OP, LOGGING_ENABLED, LOG_TO_CONSOLE, API_ENABLED,
            UPDATE_CHECKER_ENABLED, CLIENT_ENABLED, CLIENT_CASE_SENSITIVE;


    public List<String> BLOCKED_CLIENTS = new ArrayList<>();

    public List<String> ENABLED_CHECKS = new ArrayList<>();
    public List<String> SETBACK_CHECKS = new ArrayList<>();

    public Map<String, Integer> MAX_VIOLATIONS = new HashMap<>();
    public Map<String, String> PUNISH_COMMANDS = new HashMap<>();

    public void updateConfig() {
        try {
            TESTMODE = getBoolean("testmode");

            PREFIX = ColorUtil.translate(getString("response.anticheat.prefix"));
            NO_PERMS = getString("response.general.no-permission");
            COMMAND_NAME = getString("response.command.name");
            CLEAR_VIOLATIONS_DELAY = getInteger("violations.clear-violations-delay");
            COMMAND_PREFIX = ColorUtil.translate(getString("response.command.prefix"));

            CLIENT_ENABLED = getBoolean("client.enabled");
            CLIENT_CASE_SENSITIVE = getBoolean("client.case-sensitive");

            CLIENT_KICK_MESSAGE = getString("client.kick-message");
            BLOCKED_CLIENTS = getStringList("client.blocked");

            BYPASS_OP = getBoolean("bypass.bypass-operators");

            LOGGING_ENABLED = getBoolean("logging.enabled");
            LOG_FORMAT = getString("logging.log-format");
            LOG_TO_CONSOLE = getBoolean("violations.alert-console");

            API_ENABLED = getBoolean("api.enabled");

            UPDATE_CHECKER_ENABLED = getBoolean("update-checker.enabled");

            VL_TO_ALERT = getInteger("violations.minimum-vl");
            ALERT_FORMAT = getString("violations.alert-format");

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
                                value.setValue(getBoolean("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case INTEGER:
                                value.setValue(getInteger("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case DOUBLE:
                                value.setValue(getDouble("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case STRING:
                                value.setValue(getString("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case LONG:
                                value.setValue(getLong("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                        }

                        field.setAccessible(accessible);
                    }
                }

                final boolean enabled = getBoolean("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".enabled");

                Bukkit.broadcastMessage("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type().toLowerCase() + ".enabled" + " " + enabled);

                final int maxViolations = getInteger("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + ".max-violations");
                final String punishCommand = getString("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + ".punish-command");

                if (checkType.equals("movement")) {
                    final boolean setBack = getBoolean("checks.movement." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + ".setback");

                    if (setBack) {
                        SETBACK_CHECKS.add(check.getSimpleName());
                    }
                }

                if (enabled) {
                    ENABLED_CHECKS.add(check.getSimpleName());
                }

                MAX_VIOLATIONS.put(check.getSimpleName(), maxViolations);
                PUNISH_COMMANDS.put(check.getSimpleName(), punishCommand);
            }
        } catch (final Exception exception) {
            Bukkit.getLogger().severe("Could not properly load config.");
            exception.printStackTrace();
        }

    }

    private boolean getBoolean(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getBoolean(string);
    }

    private String getString(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getString(string);
    }

    private int getInteger(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getInt(string);
    }

    private double getDouble(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getDouble(string);
    }

    private long getLong(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getLong(string);
    }

    private List<String> getStringList(final String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getStringList(string);
    }
}