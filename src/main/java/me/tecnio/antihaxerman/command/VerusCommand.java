package me.tecnio.antihaxerman.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VerusCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.BLUE + "This server is running Verus version " + ChatColor.WHITE + "b2067 (dort's token generator edition)");
        return true;
    }
}
