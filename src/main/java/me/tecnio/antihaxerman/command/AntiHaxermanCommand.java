
package me.tecnio.antihaxerman.command;

import me.tecnio.antihaxerman.util.ColorUtil;
import org.bukkit.ChatColor;
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
        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "AHM" + ChatColor.GRAY + "]" + " ");
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
