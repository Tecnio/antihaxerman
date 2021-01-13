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

package me.tecnio.antihaxerman.command.impl;

import me.tecnio.antihaxerman.command.AntiHaxermanCommand;
import me.tecnio.antihaxerman.command.CommandInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "alerts", purpose = "Toggles cheat alerts.")
public final class Alerts extends AntiHaxermanCommand {

    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("antihaxerman.alerts") || sender.isOp()) {
                final Player player = (Player) sender;
                final PlayerData data = PlayerDataManager.getInstance().getPlayerData(player);

                if (data != null) {
                    if (AlertManager.toggleAlerts(data) == AlertManager.ToggleAlertType.ADD) {
                        sendMessage(sender, ColorUtil.translate("&cToggled your cheat alerts &2on&a."));
                    } else {
                        sendMessage(sender, ColorUtil.translate("&cToggled your cheat alerts &coff&a."));
                    }
                    return true;
                }
            }
        } else {
            sendMessage(sender, "Only players can execute this command.");
        }
        return false;
    }
}
