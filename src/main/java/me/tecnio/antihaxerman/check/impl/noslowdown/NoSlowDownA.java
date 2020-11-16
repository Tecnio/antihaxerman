package me.tecnio.antihaxerman.check.impl.noslowdown;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "NoSlowDown", type = "A")
public final class NoSlowDownA extends Check {
    public NoSlowDownA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final boolean exempt = data.getGroundTicks() < 10;

        final boolean invalid = data.getPlayer().isBlocking() && data.isBlocking() && data.getSprintingTicks() > 10;

        if (invalid && !exempt) {
            if (increaseBuffer() > 15) {
                flag();
            }
        } else {
            resetBuffer();
        }
    }
}
