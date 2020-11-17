/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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
