package me.tecnio.ahm.command.impl;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.tecnio.ahm.command.AHMCommand;
import org.bukkit.command.CommandSender;

@CommandAlias("ahm")
public class DefaultCommand extends AHMCommand {

    @HelpCommand
    @Description("AntiHaxerman help command")
    @CommandPermission("ahm.command")
    @Syntax("")
    public void onHelp(final CommandSender sender, final CommandHelp commandHelp) {
        sendLineBreak(sender);

        sendMessage(sender, "&cAntiHaxerman commands:");

        // whoever coded this fix this monstrosity. or ur fired
        commandHelp.getHelpEntries().forEach(command -> sendMessage(sender, " &f- &c" + command.getCommandPrefix() + command.getCommand() + " &7" + command.getParameterSyntax() + " &8- &c" + command.getDescription()));

        sendMessage(sender, String.format("&7Total of &c%s &7commands.", commandHelp.getHelpEntries().size()));
        sendLineBreak(sender);
    }
}
