

package me.tecnio.antihaxerman.command.impl;

import me.tecnio.antihaxerman.command.AntiHaxermanCommand;
import me.tecnio.antihaxerman.command.CommandInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.util.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "alerts", purpose = "Toggles cheat alerts.")
public final class Alerts extends AntiHaxermanCommand {

    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("ahm.alerts")) {
                final Player player = (Player) sender;
                final PlayerData data = PlayerDataManager.getInstance().getPlayerData(player);

                if (data != null) {
                    if (AlertManager.toggleAlerts(data) == AlertManager.ToggleAlertType.ADD) {
                        sendMessage(sender, ColorUtil.translate("&cToggled your Antihaxerman alerts &aon&a."));
                    } else {
                        sendMessage(sender, ColorUtil.translate("&cToggled your Antihaxerman alerts &coff&a."));
                    }
                    return true;
                }
            }
        } else {
            sendMessage(sender, "Only players can execute this command.");
        }
        return false;
    }
}
