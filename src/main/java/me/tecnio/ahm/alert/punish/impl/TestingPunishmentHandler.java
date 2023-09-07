package me.tecnio.ahm.alert.punish.impl;

import me.tecnio.ahm.alert.punish.PunishmentHandler;
import me.tecnio.ahm.check.Check;
import org.bukkit.Bukkit;

public class TestingPunishmentHandler implements PunishmentHandler {

    @Override
    public void punish(final Check check) {
        Bukkit.broadcastMessage("You would have been punished for " + check.getName() + " " + check.getType() + ".");
    }
}
