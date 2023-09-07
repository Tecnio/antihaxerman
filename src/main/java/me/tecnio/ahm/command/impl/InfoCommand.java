package me.tecnio.ahm.command.impl;

import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.tecnio.ahm.AHM;
import me.tecnio.ahm.command.AHMCommand;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.PlayerDataManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@CommandAlias("ahm")
public class InfoCommand extends AHMCommand {

    @Subcommand("info")
    @Description("View information about a player")
    @CommandPermission("ahm.info")
    public void onCommand(final CommandSender sender, @Name("target") final OnlinePlayer onlinePlayer) {
        final Player player = onlinePlayer.getPlayer();
        final PlayerData data = AHM.get(PlayerDataManager.class).getPlayerData(player.getUniqueId());

        final String lastTransaction = new SimpleDateFormat("hh:mm:ss:ms aaa").format(data.getConnectionTracker().getLastTransaction());
        final String lastEntity = data.getActionTracker().getTarget() == null ? "None" : data.getActionTracker().getTarget().getName();

        final List<String> message = Arrays.asList(
                "&cInformation for &f" + player.getName() + "&c:",
                " &f- &cEnabled Checks: &7" + data.getChecks().size(),
                " &f- &cTracked Entities: &7" + data.getEntityTracker().getTrackerMap().size(),
                " &f- &cLast Transaction Received: &7" + lastTransaction,
                " &f- &cLast Target: &7" + lastEntity,
                " &f- &cSensitivity: &7" + data.getRotationTracker().getSensitivity() + "%",
                " &f- &cTransaction Ping: &7" + data.getConnectionTracker().getTransactionPing() + "ms",
                " &f- &cKeep Alive Ping: &7" + data.getConnectionTracker().getKeepAlivePing() + "ms"
        );

        sendLineBreak(sender);
        sendMessage(sender, String.join("\n", message));
        sendLineBreak(sender);
    }
}
