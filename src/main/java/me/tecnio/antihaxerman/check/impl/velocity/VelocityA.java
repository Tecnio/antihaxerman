package me.tecnio.antihaxerman.check.impl.velocity;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Velocity", type = "A")
public final class VelocityA extends Check {
    public VelocityA(PlayerData data) {
        super(data);
    }

    @Override
    public void onFlying() {
        if (data.getVelocityTicks() == 1) {
            final double velTaken = data.getDeltaY();
            final double velExpected = data.getLastVelocity().getY() * 0.999F;

            final double percentage = (velTaken * 100) / velExpected;

            final boolean exempt = data.liquidTicks() < 20 || data.pistonTicks() < 10 || data.climbableTicks() < 20 || data.collidedVTicks() < 20 || data.teleportTicks() < 20 || data.flyingTicks() < 20;

            if (velTaken < velExpected && !exempt) {
                if (increaseBuffer() > 3) {
                    flag("(Vertical) percentage: " + percentage);
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
