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

package me.tecnio.antihaxerman.command;

import me.tecnio.antihaxerman.util.ColorUtil;
import me.tecnio.antihaxerman.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class AntiHaxermanCommand implements Comparable<AntiHaxermanCommand> {

    protected abstract boolean handle(final CommandSender sender, final Command command, final String label, final String[] args);

    public void sendLineBreak(final CommandSender sender) {
        sender.sendMessage(ColorUtil.translate("&8&m--------------------------------------------------"));
    }

    public void sendRetardedNewLine(final CommandSender sender) {
        sender.sendMessage("");
    }

    public void sendPrefix(final CommandSender sender) {
        sender.sendMessage(Config.COMMAND_PREFIX + " ");
    }

    public void sendMessage(final CommandSender sender, final String message) {
        sender.sendMessage(ColorUtil.translate(message));
    }

    public CommandInfo getCommandInfo() {
        if (this.getClass().isAnnotationPresent(CommandInfo.class)) {
            return this.getClass().getAnnotation(CommandInfo.class);
        } else {
            System.err.println("CommandInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    @Override
    public int compareTo(AntiHaxermanCommand o) {
        return 0;
    }
}
