package me.tecnio.ahm.command.impl;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import me.tecnio.ahm.AntiHaxerman;
import me.tecnio.ahm.alert.AlertManager;
import me.tecnio.ahm.command.AntiHaxermanCommand;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.PlayerDataManager;
import me.tecnio.ahm.util.string.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("ahm")
public class AlertsCommand extends AntiHaxermanCommand {

    @Subcommand("alerts")
    @Description("Toggles your anti-cheat alerts")
    @CommandPermission("ahm.alerts")
    public void onCommand(final CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return;
        }

        final Player player = ((Player) sender).getPlayer();
        final PlayerData data = AntiHaxerman.get(PlayerDataManager.class).get(player.getUniqueId());

        if (AntiHaxerman.get(AlertManager.class).toggleAlerts(data)) {
            player.sendMessage(ChatUtil.translate("&aYou are now viewing alerts!"));
        } else {
            player.sendMessage(ChatUtil.translate("&cYou are no longer viewing alerts!"));
        }
    }
}
