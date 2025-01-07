package me.tecnio.ahm.alert.punish.impl;

import me.tecnio.ahm.AntiHaxerman;
import me.tecnio.ahm.alert.punish.PunishmentHandler;
import me.tecnio.ahm.check.Check;
import org.bukkit.Bukkit;

public class ProductionPunishmentHandler implements PunishmentHandler {

    @Override
    public void punish(final Check check) {
        if (!check.isPunishing()) return;

        check.getPunishments().forEach(s -> {
            this.execute(s.replace("%player%", check.getData().getPlayer().getName()));;
        });
    }

    private void execute(final String command) {
        Bukkit.getScheduler().runTask(AntiHaxerman.get().getPlugin(),
                () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }
}
