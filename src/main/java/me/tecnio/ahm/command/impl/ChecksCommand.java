package me.tecnio.ahm.command.impl;

import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.tecnio.ahm.AHM;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.command.AHMCommand;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.PlayerDataManager;
import me.tecnio.ahm.util.string.ChatUtil;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@CommandAlias("ahm")
public class ChecksCommand extends AHMCommand {

    @Subcommand("checks")
    @Description("View enabled checks for player")
    @CommandPermission("ahm.checks")
    public void onCommand(final CommandSender sender, @Name("target") final OnlinePlayer onlinePlayer) {
        final Player player = onlinePlayer.getPlayer();
        final PlayerData data = AHM.get(PlayerDataManager.class).getPlayerData(player.getUniqueId());

        sendLineBreak(sender);
        sendMessage(sender, "&cChecks for &f" + player.getName() + "&c:");

        for (Check check : data.getChecks()) {
            final String message = ChatUtil.translate(" &f- &c" + check.getName() + "&7(&c" + check.getType() + "&7) &8- &c" + check.getState().getDisplayName());

            final List<String> hover = Arrays.asList(
                    "&7" + check.getDescription(),
                    " ",
                    "&cMaxVl: &f" + check.getMaxVl(),
                    "&cState: &f" + check.getState().getDisplayName(),
                    " ",
                    "&cCommands:",
                    (check.getPunishments().isEmpty() ? "&fEmpty" : String.join("\n", check.getPunishments()))
            );

            final TextComponent checksFormat = new TextComponent(message);
            final BaseComponent[] baseComponents = new ComponentBuilder(ChatUtil.translate(String.join("\n", hover))).create();

            checksFormat.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, baseComponents));

            if (sender instanceof Player) {
                final Player player1 = (Player) sender;

                player1.spigot().sendMessage(checksFormat);
            } else {
                sender.sendMessage(ChatUtil.translate(message));
            }
        }

        sendLineBreak(sender);
    }
}
