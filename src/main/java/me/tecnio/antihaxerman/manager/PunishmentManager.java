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

package me.tecnio.antihaxerman.manager;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.api.APIManager;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.util.LogUtil;
import me.tecnio.antihaxerman.util.ServerUtil;
import org.bukkit.Bukkit;

public final class PunishmentManager {
    public static void punish(final Check check, final PlayerData data) {
        APIManager.callPunishEvent(check);

        if (check.getPunishCommand() != null) {
            Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () ->
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), check.getPunishCommand()
                            .replaceAll("%player%", data.getPlayer().getName())
                            .replaceAll("%prefix%", Config.PREFIX)
                            .replaceAll("%check%", check.getCheckInfo().name())
                            .replaceAll("%type%", check.getCheckInfo().type())));
        }

        if (Config.LOGGING_ENABLED) {
            final String log = String.format("###\n%s has been punished for %s (Type %s)\n" +
                            "Info:\n" +
                            "kaPing: %.2d tPing: %.2d lag: %s\n" +
                            "TPS: %.2f\n" +
                            "deltaXZ: %.4f deltaY: %.4f" +
                            "\n###",
                    data.getPlayer().getName(), check.getCheckInfo().name(),
                    check.getCheckInfo().type(), data.getConnectionProcessor().getKeepAlivePing(),
                    data.getConnectionProcessor().getTransactionPing(), data.getExemptProcessor().isExempt(ExemptType.LAGGING),
                    ServerUtil.getTPS(), data.getPositionProcessor().getDeltaXZ(), data.getPositionProcessor().getDeltaY());

            LogUtil.logToFile(data.getLogFile(), log);
        }
    }
}
