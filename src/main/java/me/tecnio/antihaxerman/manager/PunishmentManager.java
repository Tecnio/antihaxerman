package me.tecnio.antihaxerman.manager;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.Config;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.other.ChatUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public final class PunishmentManager {
    public static void punish(Check check, PlayerData data) {
        if (check.getCheckInfo().autoBan()) {
            final List<String> punishments = Config.PUNISH_COMMANDS.getOrDefault(check.getCheckInfo().name(), new ArrayList<>());

            for (final String cmd : punishments) {
                final String command = ChatUtils.color(cmd.replaceAll("%player%", data.getPlayer().getName()).replaceAll("%check%", check.getCheckInfo().name()).replaceAll("%checktype%", check.getCheckInfo().type()));

                Bukkit.getScheduler().runTask(AntiHaxerman.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            }
        }
    }
}
