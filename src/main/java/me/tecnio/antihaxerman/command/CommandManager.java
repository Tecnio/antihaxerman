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

import me.tecnio.antihaxerman.AntiHaxermanPlugin;
import me.tecnio.antihaxerman.command.impl.*;
import me.tecnio.antihaxerman.util.ColorUtil;
import me.tecnio.antihaxerman.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommandManager implements CommandExecutor {

    private final List<AntiHaxermanCommand> commands = new ArrayList<>();

    public CommandManager(final AntiHaxermanPlugin plugin) {
        commands.add(new Alerts());
        commands.add(new Checks());
        commands.add(new Info());
        commands.add(new Exempt());
        commands.add(new Debug());

        Collections.sort(commands);
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String string, final String[] args) {
        if (commandSender.hasPermission("antihaxerman.commands") || commandSender.isOp()) {
            if (args.length > 0) {
                for (final AntiHaxermanCommand antiHaxermanCommand : commands) {
                    final String commandName = antiHaxermanCommand.getCommandInfo().name();
                    if (commandName.equals(args[0])) {
                        if (!antiHaxermanCommand.handle(commandSender, command, string, args)) {
                            commandSender.sendMessage(Config.COMMAND_PREFIX  + " Usage: /antihaxerman " +
                                    antiHaxermanCommand.getCommandInfo().name() + " " +
                                    antiHaxermanCommand.getCommandInfo().syntax());
                        }
                        return true;
                    }
                }
            } else {
                commandSender.sendMessage(ColorUtil.translate("&8&m--------------------------------------------------"));
                commandSender.sendMessage(ColorUtil.translate("&cAntiHaxerman Commands:\n" + " \n"));
                for (final AntiHaxermanCommand antiHaxermanCommand : commands) {
                    commandSender.sendMessage(ColorUtil.translate( "&c/antihaxerman " +
                            antiHaxermanCommand.getCommandInfo().name() + " " +
                            antiHaxermanCommand.getCommandInfo().syntax()));
                }
                commandSender.sendMessage(ColorUtil.translate("&8&m--------------------------------------------------"));
                return true;
            }
        }
        return false;
    }
}
