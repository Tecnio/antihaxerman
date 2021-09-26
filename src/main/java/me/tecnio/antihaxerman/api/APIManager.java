

package me.tecnio.antihaxerman.api;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.api.impl.AHMFlagEvent;
import me.tecnio.antihaxerman.api.impl.AHMPunishEvent;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.config.Config;
import org.bukkit.Bukkit;

public final class APIManager {
    public static void callFlagEvent(final Check check) {
        if (!Config.API_ENABLED) return;

        final AHMFlagEvent flagEvent = new AHMFlagEvent(
                check.getData().getPlayer(),
                check.getCheckInfo().name(),
                check.getCheckInfo().type(),
                check.getVl(),
                check.getBuffer()
        );

        Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getPluginManager().callEvent(flagEvent));
    }

    public static void callPunishEvent(final Check check) {
        if (!Config.API_ENABLED) return;
        final AHMPunishEvent punishEvent = new AHMPunishEvent(
                check.getData().getPlayer(),
                check.getCheckInfo().name(),
                check.getCheckInfo().type(),
                check.getPunishCommands(),
                check.getVl(),
                check.getBuffer()
        );

        Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getPluginManager().callEvent(punishEvent));
    }
}
