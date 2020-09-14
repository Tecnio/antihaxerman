package me.tecnio.antihaxerman.checks.impl.movement.motion;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "Motion", type = "D")
public final class MotionD extends Check {
    @Override
    public void onMove(PlayerData data) {
        if (data.getDeltaY() != 0.0D && Math.abs(data.getDeltaY()) <= 0.005D) {
            flag(data, "invalid motion. d: " + data.getDeltaY());
        }
    }
}
