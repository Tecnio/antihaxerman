package me.tecnio.antihaxerman.checks.impl.movement.speed;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.checks.SetBackType;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "Speed", type = "A")
public final class SpeedA extends Check {
    @Override
    public void onMove(PlayerData data) {
        if (data.getAirTicks() > 2 && !data.getPlayer().isFlying() && !PlayerUtils.inLiquid(data) && !data.isTakingVelocity() && !PlayerUtils.isOnWeirdBlock(data) && !data.isUnderBlock() && data.teleportTicks() > 5){
            final double prediction = data.getLastDeltaXZ() * 0.91F + (data.isSprinting() ? 0.0263 : 0.02);
            final double diff = data.getDeltaXZ() - prediction;

            if (diff > 1E-12) {
                flag(data, "ignored friction at air! diff: " + diff, SetBackType.BACK);
            }
        }
    }
}
