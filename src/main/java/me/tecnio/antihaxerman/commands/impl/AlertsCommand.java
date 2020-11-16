package me.tecnio.antihaxerman.commands.impl;

import me.tecnio.antihaxerman.commands.api.CommandAdapter;
import me.tecnio.antihaxerman.commands.api.UserInput;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import org.bukkit.entity.Player;

public final class AlertsCommand extends CommandAdapter {
    @Override
    public boolean onCommand(Player player, UserInput input) {
        if (!player.isOp() && !player.hasPermission("antihaxerman.alerts")) return false;

        if (input.label().equalsIgnoreCase("alerts")) {
            PlayerData data = PlayerDataManager.getPlayerData().get(player.getUniqueId());

            if (data != null) {
                if (data.isAlerts()) {
                    sendMessage(player, "Toggled off alerts.");
                } else {
                    sendMessage(player, "Toggled on alerts.");
                }

                data.toggleAlerts();

                return true;
            }
        }

        return false;
    }
}
