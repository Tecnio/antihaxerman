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

package me.tecnio.antihaxerman.commands.impl;

import me.tecnio.antihaxerman.commands.api.CommandAdapter;
import me.tecnio.antihaxerman.commands.api.UserInput;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import org.bukkit.entity.Player;

public final class VerboseCommand extends CommandAdapter {
    @Override
    public boolean onCommand(Player player, UserInput input) {
        if (!player.isOp() && !player.hasPermission("antihaxerman.alerts")) return false;

        if (input.label().equalsIgnoreCase("verbose")) {
            final PlayerData data = PlayerDataManager.getPlayerData().get(player.getUniqueId());

            if (data != null) {
                if (data.isVerbose()) {
                    sendMessage(player, "Toggled off verbose alerts.");
                } else {
                    sendMessage(player, "Toggled on verbose alerts.");
                }

                data.toggleVerbose();

                return true;
            }
        }

        return false;
    }
}
