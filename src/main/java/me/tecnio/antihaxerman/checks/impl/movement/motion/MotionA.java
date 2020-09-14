package me.tecnio.antihaxerman.checks.impl.movement.motion;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "Motion", type = "A")
public final class MotionA extends Check {
    @Override
    public void onMove(PlayerData data) {
        if (!PlayerUtils.blockNearHead(data) && !data.getLocation().clone().subtract(0,0.2,0).getBlock().getType().toString().toUpperCase().contains("SLIME") && data.teleportTicks() > 10) {
            if (data.getDeltaY() == -data.getLastDeltaY() && data.getDeltaY() != 0) {
                if (++preVL > 3) {
                    flag(data, "repetitive vertical motions, m: " + data.getDeltaY());
                }
            } else preVL = 0;
        }
    }
}
