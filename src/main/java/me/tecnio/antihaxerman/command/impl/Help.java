package me.tecnio.antihaxerman.command.impl;

import me.tecnio.antihaxerman.command.AntiHaxermanCommand;
import me.tecnio.antihaxerman.command.CommandInfo;
import me.tecnio.antihaxerman.command.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandInfo(name = "help", purpose = "Prints every command.")
public class Help extends AntiHaxermanCommand {
    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        for (final AntiHaxermanCommand AntiHaxermancommand : CommandManager.getInstance().commands) {
            final String commandName = AntiHaxermancommand.getCommandInfo().name();
            if (commandName.equals(args[0])) {
                    sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "AntiHaxerman" + ChatColor.GRAY + "]" + " Usage: /ahm " +
                            AntiHaxermancommand.getCommandInfo().name() + " " +
                            AntiHaxermancommand.getCommandInfo().syntax());
                return true;
            }
        }
        return false;
    }
}
