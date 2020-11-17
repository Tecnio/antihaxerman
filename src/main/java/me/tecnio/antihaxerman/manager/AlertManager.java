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

package me.tecnio.antihaxerman.manager;

import io.github.retrooper.packetevents.PacketEvents;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.Config;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.other.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AlertManager {

    private static final ExecutorService alertExecutor = Executors.newSingleThreadExecutor();

    public static List<PlayerData> playersWithAlerts = new ArrayList<>();
    public static List<PlayerData> playersWithVerbose= new ArrayList<>();

    public static void toggleAlerts(PlayerData data) {
        if (!playersWithAlerts.contains(data)) {
            playersWithAlerts.add(data);
        } else {
            playersWithAlerts.remove(data);
        }
    }

    public static void toggleVerbose(PlayerData data) {
        if (!playersWithVerbose.contains(data)) {
            playersWithVerbose.add(data);
        } else {
            playersWithVerbose.remove(data);
        }
    }

    private static final String alertFormat = ChatUtils.color(Config.ALERT_FORMAT);

    public static void alertCheck(PlayerData data, Check check, String information) {
        final String message = alertFormat
                .replaceAll("%player%", data.getPlayer().getName())
                .replaceAll("%check%", check.getCheckInfo().name())
                .replaceAll("%type%", check.getCheckInfo().type())
                .replaceAll("%vl%", "" + check.getVl());

        if (Config.LOG_TO_CONSOLE) AntiHaxerman.getInstance().getLogger().info(message);

        alertExecutor.execute(() -> {
            TextComponent alertMessage = new TextComponent(message);

            alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
            alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtils.color("Info:\n" + information + "\n" +
                    "\nPing: " + data.getKeepAlivePing() +
                    "\nTPS: " + PacketEvents.getAPI().getServerUtils().getTPS() +
                    "\n&cClick to teleport!")).create()));

            playersWithAlerts.forEach(dataToAlert -> {
                if (Math.abs(dataToAlert.getLastAlertMessage() - System.currentTimeMillis()) > 1000) {
                    dataToAlert.getPlayer().spigot().sendMessage(alertMessage);
                    dataToAlert.setLastAlertMessage(System.currentTimeMillis());
                }
            });

            playersWithVerbose.forEach(dataToAlert -> dataToAlert.getPlayer().spigot().sendMessage(alertMessage));
        });
    }
}
