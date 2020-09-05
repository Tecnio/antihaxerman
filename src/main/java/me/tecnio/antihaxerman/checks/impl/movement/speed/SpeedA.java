package me.tecnio.antihaxerman.checks.impl.movement.speed;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.checks.SetBackType;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "Speed", type = "A")
public class SpeedA extends Check {

    private boolean lastOnGround, lastLastOnGround;

    @Override
    public void onMove(PlayerData data) {
        if (!data.isOnGround() && !lastOnGround && !lastLastOnGround && !data.getPlayer().isFlying() && !PlayerUtils.inLiquid(data) && !data.isTakingVelocity() && !PlayerUtils.isOnWeirdBlock(data) && !data.isUnderBlock() && data.teleportTicks() > 5){
            double predicted = data.getLastDeltaXZ() * 0.91F;
            double diff = data.getDeltaXZ() - predicted;

            if (diff > 0.026) {
                if (++preVL > 1) {
                    flag(data, "ignored friction at air! diff: " + diff, SetBackType.BACK);
                }
            } else preVL = 0;
        }
        lastLastOnGround = lastOnGround;
        lastOnGround = data.isOnGround();
    }
}
