package me.tecnio.antihaxerman.checks.impl.movement.invalid;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.checks.SetBackType;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "Invalid", type = "A")
public final class InvalidA extends Check {
    @Override
    public void onMove(PlayerData data) {
        if (data.isOnClimbableBlock() && !data.isTakingVelocity() && !data.isOnGround() && !data.isInWeb() && !data.getPlayer().isInsideVehicle() && !data.getPlayer().isFlying()){
            if(data.getDeltaY() < 0 && data.isSneaking()) {
                if(preVL++ >= 3) {
                    flag(data, "moving downwards while sneaking on a climbable", SetBackType.BACK);
                    preVL = 0;
                }
            } else preVL = Math.max(0, preVL--);
        }
    }
}
