package me.tecnio.antihaxerman.check.impl.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Motion", type = "C")
public final class MotionC extends Check {
    public MotionC(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final double deltaY = data.getDeltaY();

        final boolean exempt = data.teleportTicks() < 20 || data.getTick() < 20;

        if (deltaY < -3.92 && !exempt) {
            flag();
        }
    }
}
