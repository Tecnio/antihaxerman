package me.tecnio.antihaxerman.checks.impl.movement.invalid;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "Invalid", type = "A")
public final class InvalidA extends Check {
    public InvalidA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        if (data.isOnClimbableBlock() && !data.isTakingVelocity() && !data.isOnGround() && !data.isInWeb() && !data.getPlayer().isInsideVehicle() && !data.getPlayer().isFlying()){
            if(data.getDeltaY() < 0 && data.isSneaking()) {
                if(buffer++ >= 3) {
                    flag(data, "moving downwards while sneaking on a climbable");
                    buffer = 0;
                }
            } else buffer = Math.max(0, buffer--);
        }
    }
}
