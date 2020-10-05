package me.tecnio.antihaxerman.checks.impl.movement.motion;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "Motion", type = "A")
public final class MotionA extends Check {
    public MotionA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        if (!PlayerUtils.blockNearHead(data) && !PlayerUtils.isOnSlime(data) && data.teleportTicks() > 10) {
            if (data.getDeltaY() == -data.getLastDeltaY() && data.getDeltaY() != 0) {
                if (++buffer > 3) {
                    flag(data, "repetitive vertical motions, m: " + data.getDeltaY());
                }
            } else buffer = 0;
        }
    }
}
