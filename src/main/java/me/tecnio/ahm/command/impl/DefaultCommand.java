package me.tecnio.ahm.command.impl;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.tecnio.ahm.command.AntiHaxermanCommand;
import org.bukkit.command.CommandSender;

@CommandAlias("ahm")
public class DefaultCommand extends AntiHaxermanCommand {

    @HelpCommand
    @Description("AntiHaxerman help command")
    @CommandPermission("ahm.command")
    @Syntax("")
    public void onHelp(final CommandSender sender, final CommandHelp commandHelp) {
        sendLineBreak(sender);

        sendMessage(sender, "&cAntiHaxerman commands:");

        commandHelp.getHelpEntries().forEach(command -> sendMessage(sender, " &f- &c" + command.getCommandPrefix() +
                command.getCommand() + " &7" + command.getParameterSyntax() + " &8- &c" + command.getDescription()));

        sendMessage(sender, String.format("&7Total of &c%s &7commands.", commandHelp.getHelpEntries().size()));
        sendLineBreak(sender);
    }
}
