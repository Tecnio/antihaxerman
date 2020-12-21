/*
 *  Copyright (C) 2020 Tecnio
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

import io.github.retrooper.packetevents.event.threadmode.PacketListenerThreadMode;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.manager.CheckManager;
import me.tecnio.antihaxerman.util.ColorUtil;
import org.bukkit.Bukkit;
import java.lang.reflect.Field;
import java.util.*;

public final class Config {

    public static boolean TESTMODE;

    public static String PREFIX;
    public static String NO_PERMS;
    public static String COMMAND_PREFIX;

    public static int VL_TO_ALERT;
    public static String ALERT_FORMAT;
    public static String COMMAND_NAME;
    public static int CLEAR_VIOLATIONS_DELAY;

    public static PacketListenerThreadMode THREADING;

    public static List<String> ENABLED_CHECKS = new ArrayList<>();
    public static List<String> SETBACK_CHECKS = new ArrayList<>();
    public static Map<String, Integer> MAX_VIOLATIONS = new HashMap<>();
    public static Map<String, String> PUNISH_COMMANDS = new HashMap<>();


    public static void updateConfig() {
        try {
            TESTMODE = getBooleanFromConfig("testmode");

            PREFIX = ColorUtil.translate(getStringFromConfig("response.anticheat.prefix"));
            NO_PERMS = getStringFromConfig("response.general.no-permission");
            COMMAND_NAME = getStringFromConfig("response.command.name");
            CLEAR_VIOLATIONS_DELAY = getIntegerFromConfig("violations.clear-violations-delay");
            COMMAND_PREFIX = ColorUtil.translate(getStringFromConfig("response.command.prefix"));

            final String threading = getStringFromConfig("system.threading");

            if (threading.equalsIgnoreCase("netty")) {
                THREADING = PacketListenerThreadMode.NETTY;
            } else {
                THREADING = PacketListenerThreadMode.PACKETEVENTS;
            }

            VL_TO_ALERT = getIntegerFromConfig("violations.minimum-vl");
            ALERT_FORMAT = getStringFromConfig("violations.alert-format");

            for (Class check : CheckManager.CHECKS) {
                final CheckInfo checkInfo = (CheckInfo) check.getAnnotation(CheckInfo.class);
                String checkType = "";
                if (check.getName().contains("combat")) {
                    checkType = "combat";
                } else if (check.getName().contains("movement")) {
                    checkType = "movement";
                } else if (check.getName().contains("player")) {
                    checkType = "player";
                }

                for (Field field : check.getDeclaredFields()) {
                    if (field.getType().equals(ConfigValue.class)) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        String name = ((ConfigValue) field.get(null)).getName();
                        ConfigValue value = ((ConfigValue) field.get(null));
                        ConfigValue.ValueType type = value.getType();

                        switch (type) {
                            case BOOLEAN:
                                value.setValue(getBooleanFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case INTEGER:
                                value.setValue(getIntegerFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case DOUBLE:
                                value.setValue(getDoubleFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case STRING:
                                value.setValue(getStringFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                            case LONG:
                                value.setValue(getLongFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + "." + name));
                                break;
                        }
                        field.setAccessible(accessible);
                    }
                }

                final boolean enabled = getBooleanFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + String.valueOf(checkInfo.type()).toLowerCase() + ".enabled");

                Bukkit.broadcastMessage("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + String.valueOf(checkInfo.type()).toLowerCase() + ".enabled" + " " + enabled);
                final int maxViolations = getIntegerFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + ".max-violations");
                final String punishCommand = getStringFromConfig("checks." + checkType + "." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + ".punish-command");

                if (checkType.equals("movement")) {
                    final boolean setBack = getBooleanFromConfig("checks.movement." + checkInfo.name().toLowerCase() + "." + checkInfo.type() + ".setback");
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
        } catch (Exception exception) {
            Bukkit.getLogger().severe("Could not properly load config.");
            exception.printStackTrace();
        }

    }

    private static boolean getBooleanFromConfig(String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getBoolean(string);
    }

    public static String getStringFromConfig(String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getString(string);
    }

    private static int getIntegerFromConfig(String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getInt(string);
    }

    private static double getDoubleFromConfig(String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getDouble(string);
    }

    private static long getLongFromConfig(String string) {
        return AntiHaxerman.INSTANCE.getPlugin().getConfig().getLong(string);
    }
}