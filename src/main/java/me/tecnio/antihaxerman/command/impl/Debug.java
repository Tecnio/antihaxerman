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

package me.tecnio.antihaxerman.command.impl;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.command.AntiHaxermanCommand;
import me.tecnio.antihaxerman.command.CommandInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "debug", purpose = "Allows the player to debug checks.", syntax = "<check> <checktype>")
public final class Debug extends AntiHaxermanCommand {
    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 3) {
            final PlayerData data = PlayerDataManager.getInstance().getPlayerData(((Player) sender));

            final String checkName = args[1];
            final String checkType = args[2];

            if (data != null && checkName != null && checkType != null) {
                Check check = null;

                for (final Check c : data.getChecks()) {
                    if (c.getCheckInfo().name().equalsIgnoreCase(checkName)
                            && c.getCheckInfo().type().equalsIgnoreCase(checkType)) {
                        check = c;
                        break;
                    }
                }

                if (check != null) {
                    if (check.isDebug()) {
                        check.setDebug(false);
                        sendMessage(sender, String.format("Disabled debugging for the check %s (Type %s)", check.getCheckInfo().name(), check.getCheckInfo().type()));
                    } else {
                        check.setDebug(true);
                        sendMessage(sender, String.format("Enabled debugging for the check %s (Type %s)", check.getCheckInfo().name(), check.getCheckInfo().type()));
                    }
                } else {
                    sendMessage(sender, "Check does not exist.");
                }

                return true;
            }
        }
        return false;
    }
}
