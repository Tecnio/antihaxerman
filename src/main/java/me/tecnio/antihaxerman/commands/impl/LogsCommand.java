package me.tecnio.antihaxerman.commands.impl;

import me.tecnio.antihaxerman.commands.api.CommandAdapter;
import me.tecnio.antihaxerman.commands.api.UserInput;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public final class LogsCommand extends CommandAdapter {
    @Override
    public boolean onCommand(Player player, UserInput input) {
        if (!player.isOp() && !player.hasPermission("antihaxerman.logs")) return false;

        if (input.label().equalsIgnoreCase("logs")) {
            if (input.args().length > 0) {
                final Player targetPlayer = Bukkit.getPlayer(input.args()[0]);

                if (targetPlayer != null) {
                    final PlayerData targetData = PlayerDataManager.getPlayerData().get(targetPlayer.getUniqueId());
                    AtomicBoolean hasViolations = new AtomicBoolean(false);

                    if (targetData != null) {
                        sendLineBreak(player);
                        targetData.getChecks().forEach(check -> {
                            if (check.getVl() > 0) {
                                sendMessage(player, String.format("%s (%s) Violation Level: %s", check.getCheckInfo().name(), check.getCheckInfo().type(), check.getVl()));
                                hasViolations.set(true);
                            }
                        });
                        if (!hasViolations.get()) sendMessage(player, "The player has no violations!");
                        sendLineBreak(player);
                    }
                }
            } else {
                sendMessage(player, "Please choose a player!");
            }
            return true;
        }

        return false;
    }
}
