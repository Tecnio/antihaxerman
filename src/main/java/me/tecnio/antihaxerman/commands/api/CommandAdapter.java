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

package me.tecnio.antihaxerman.commands.api;

import me.tecnio.antihaxerman.utils.other.ChatUtils;
import org.bukkit.entity.Player;

public abstract class CommandAdapter {

    public abstract boolean onCommand(Player player, UserInput input);

    protected void sendMessage(Player player, String input) {
        player.sendMessage(ChatUtils.color("&cAntiHaxerman &8> " + input));
    }

    protected void sendLineBreak(Player player) {
        player.sendMessage(ChatUtils.color("&c------------------------------------------------"));
    }
}
