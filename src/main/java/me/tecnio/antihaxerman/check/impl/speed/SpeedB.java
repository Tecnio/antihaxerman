package me.tecnio.antihaxerman.check.impl.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Speed", type = "B")
public final class SpeedB extends Check {
    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final double prediction = data.getLastDeltaXZ() * 0.91F + 0.02 + (data.isSprinting() ? 0.0063 : 0.0);
        final double diff = data.getDeltaXZ() - prediction;

        final boolean exempt = data.getDeltaXZ() < 0.1 || data.pistonTicks() < 10 || data.getAirTicks() < 3 || data.getPlayer().isFlying() || data.liquidTicks() < 10 || data.isTakingVelocity() || data.teleportTicks() < 10 || data.collidedVTicks() < 10 || data.getPlayer().isInsideVehicle();

        if (diff > 1E-12 && prediction > 0.075 && !exempt) {
            if (increaseBuffer() > 5) {
                flag();
            }
        } else {
            decreaseBufferBy(2.5);
        }
    }
}
