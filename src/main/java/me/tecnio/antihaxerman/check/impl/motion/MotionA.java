package me.tecnio.antihaxerman.check.impl.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Motion", type = "A")
public final class MotionA extends Check {
    public MotionA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final boolean exempt = data.collidedVTicks() < 10 || data.pistonTicks() < 10 || data.slimeTicks() < 10 || data.teleportTicks() < 10;
        final boolean invalid = data.getDeltaY() == -data.getLastDeltaY() && data.getDeltaY() != 0.0;

        if (invalid && !exempt) {
            if (increaseBuffer() > 4) {
                flag();
            }
        } else {
            resetBuffer();
        }
    }
}
