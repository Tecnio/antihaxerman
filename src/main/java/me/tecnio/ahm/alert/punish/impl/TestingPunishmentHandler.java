package me.tecnio.ahm.alert.punish.impl;

import me.tecnio.ahm.alert.punish.PunishmentHandler;
import me.tecnio.ahm.check.Check;

public class TestingPunishmentHandler implements PunishmentHandler {

    @Override
    public void punish(final Check check) {
        check.getData().getPlayer().sendMessage(
                "You would have been punished for " + check.getName() + " " + check.getType() + "."
        );
    }
}
