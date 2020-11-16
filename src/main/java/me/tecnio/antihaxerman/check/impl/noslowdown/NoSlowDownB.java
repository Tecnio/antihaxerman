package me.tecnio.antihaxerman.check.impl.noslowdown;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "NoSlowDown", type = "B")
public final class NoSlowDownB extends Check {
    public NoSlowDownB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final boolean exempt = data.getGroundTicks() > 10;

        final boolean invalid = data.isSneaking() && data.isSprinting();

        if (invalid && !exempt) {
            if (increaseBuffer() > 10) {
                flag();
            }
        } else {
            decreaseBufferBy(2.5);
        }
    }
}
