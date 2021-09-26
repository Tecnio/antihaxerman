

package me.tecnio.antihaxerman.command;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.AntiHaxermanPlugin;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.util.ColorUtil;
import me.tecnio.antihaxerman.command.impl.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommandManager implements CommandExecutor {

    public final List<AntiHaxermanCommand> commands = new ArrayList<>();

    private static CommandManager instance;

    public CommandManager(final AntiHaxermanPlugin plugin) {
        instance = this;
        commands.add(new Alerts());
        commands.add(new Info());
        commands.add(new Debug());
        commands.add(new Help());
        commands.add(new Ban());
        commands.add(new Checks());
        commands.add(new Exempt());
        commands.add(new Logs());
        commands.add(new ForceBot());
        commands.add(new Crash());
        commands.add(new Gui());


        Collections.sort(commands);
    }


    public static CommandManager getInstance() {
        return instance;
    }
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String string, final String[] args) {
        if (commandSender.hasPermission("antihaxerman.commands") || commandSender.isOp()) {
            if (args.length > 0) {
                for (final AntiHaxermanCommand antihaxerman : commands) {
                    final String commandName = antihaxerman.getCommandInfo().name();
                    if (commandName.equals(args[0])) {
                        if (!antihaxerman.handle(commandSender, command, string, args)) {
                            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', Config.PREFIX) + " Usage: /antihaxerman " +
                                    antihaxerman.getCommandInfo().name() + " " +
                                    antihaxerman.getCommandInfo().syntax());
                        }
                        return true;
                    }
                }
            } else {
                commandSender.sendMessage(ColorUtil.translate("&8&m--------------------------------------------------"));
                commandSender.sendMessage(ColorUtil.translate("&cAHM Commands:\n" + " \n"));
                for (final AntiHaxermanCommand AntiHaxermancommand : commands) {
                    commandSender.sendMessage(ColorUtil.translate("&c/ahm " +
                            AntiHaxermancommand.getCommandInfo().name() + " " +
                            AntiHaxermancommand.getCommandInfo().syntax()));
                }
                commandSender.sendMessage(" ");
                commandSender.sendMessage(ColorUtil.translate("&8&m--------------------------------------------------"));
                return true;
            }
        }
        else {
            commandSender.sendMessage(ColorUtil.translate(Config.PREFIX + "Made by Tecnio/5170 :goodangel: (" + AntiHaxerman.INSTANCE.getUpdateChecker().getCurrentVersion() + ")"));
            return true;
        }
        return false;
    }
}
