package me.tecnio.antihaxerman.check.impl.largemove;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "LargeMove", type = "A", maxVL = 5)
public final class LargeMoveA extends Check {
    public LargeMoveA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final boolean exempt = data.isTakingVelocity() || data.pistonTicks() < 10 || data.teleportTicks() < 20 || data.getTick() < 20;

        if (data.getDeltaXZ() > 10.0 && !exempt) {
            flag();
        }
    }
}
