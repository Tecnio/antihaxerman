package me.tecnio.antihaxerman.check.impl.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "BadPackets", type = "D")
public final class BadPacketsD extends Check {
    public BadPacketsD(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        if (data.getDeltaY() == 0.0 && data.getGroundTicks() == 1 && data.getAirTicks() == 0) {
            if (increaseBuffer() > 5) {
                flag();
            }
        } else {
            resetBuffer();
        }
    }
}
