package me.tecnio.ahm.alert.punish;

import me.tecnio.ahm.alert.punish.impl.ProductionPunishmentHandler;
import me.tecnio.ahm.alert.punish.impl.TestingPunishmentHandler;
import me.tecnio.ahm.util.Factory;

public class PunishmentHandlerFactory implements Factory<PunishmentHandler> {

    private boolean testing;

    public PunishmentHandlerFactory setTesting(final boolean testing) {
        this.testing = testing;

        return this;
    }

    @Override
    public PunishmentHandler build() {
        return this.testing ? new TestingPunishmentHandler() : new ProductionPunishmentHandler();
    }
}
