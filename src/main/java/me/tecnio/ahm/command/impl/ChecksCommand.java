package me.tecnio.ahm.command.impl;

import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.tecnio.ahm.AHM;
import me.tecnio.ahm.command.AHMCommand;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.PlayerDataManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        data.getChecks().forEach(check -> sendMessage(sender, " &f- &c" + check.getName() + "&7(&c" + check.getType() + "&7) &8- &c" + check.getState().getDisplayName()));

        sendLineBreak(sender);
    }
}
