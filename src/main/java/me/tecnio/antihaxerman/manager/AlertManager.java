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

package me.tecnio.antihaxerman.manager;

import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.ColorUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashSet;
import java.util.Set;

@Getter
public final class AlertManager {

    private static final Set<PlayerData> alerts = new HashSet<>();

    public static ToggleAlertType toggleAlerts(final PlayerData data) {
        if (alerts.contains(data)) {
            alerts.remove(data);
            return ToggleAlertType.REMOVE;
        } else {
            alerts.add(data);
            return ToggleAlertType.ADD;
        }
    }

    public static void handleAlert(final Check check, final PlayerData data, final String info) {
        final TextComponent alertMessage = new TextComponent(ColorUtil.translate(Config.ALERT_FORMAT)
                .replaceAll("%player%", data.getPlayer().getName())
                .replaceAll("%check%", check.getCheckInfo().name())
                .replaceAll("%dev%", check.getCheckInfo().experimental() ? ColorUtil.translate("&7*") : "")
                .replaceAll("%vl%", Integer.toString(check.getVl()))
                .replaceAll("%type%", check.getCheckInfo().type()));

        alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
        alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.translate(
                "&cDescription: &f" + check.getCheckInfo().description() +
                "\n&cInfo: &7" + info +
                "\n&cPing: &7" + PacketEvents.get().getPlayerUtils().getPing(data.getPlayer()) +
                "\n&cTPS: &7" + String.format("%.2f", Math.min(20, PacketEvents.get().getServerUtils().getTPS())) +
                "\n&cClick to teleport.")).create()));

        alerts.forEach(player -> player.getPlayer().spigot().sendMessage(alertMessage));
    }

    public static void sendMessage(final String message) {
        alerts.forEach(player -> player.getPlayer().sendMessage(Config.PREFIX + message));
    }

    public enum ToggleAlertType {
        ADD, REMOVE
    }
}
