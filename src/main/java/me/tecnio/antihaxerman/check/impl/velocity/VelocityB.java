package me.tecnio.antihaxerman.check.impl.velocity;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Velocity", type = "B")
public final class VelocityB extends Check {
    public VelocityB(PlayerData data) {
        super(data);
    }

    @Override
    public void onFlying() {
        if (data.getVelocityTicks() == 1) {
            final double expectedVelX = data.getLastVelocity().getX() * (data.attackTicks() < 2 ? 0.6 : 1.0);
            final double expectedVelZ = data.getLastVelocity().getZ() * (data.attackTicks() < 2 ? 0.6 : 1.0);
            final double expectedHorizontalVel = Math.hypot(expectedVelX, expectedVelZ);

            final double velTaken = data.getDeltaXZ();

            final double percentage = (velTaken / expectedHorizontalVel) * 100;

            final boolean exempt = data.liquidTicks() < 20 || data.pistonTicks() < 10 || data.teleportTicks() < 20 || data.isInWeb() || data.collidedHTicks() < 20;

            if (exempt) return;

            if (percentage < 30 || percentage > 300) {
                if (increaseBuffer() > 3) {
                    flag();
                }
            } else {
                decreaseBufferBy(1.5);
            }
        }
    }
}
