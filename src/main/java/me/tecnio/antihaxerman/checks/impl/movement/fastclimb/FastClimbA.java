package me.tecnio.antihaxerman.checks.impl.movement.fastclimb;

import me.tecnio.antihaxerman.Config;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "FastClimb", type = "A")
public final class FastClimbA extends Check {
    public FastClimbA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        if (data.getLocation().getY() == data.getLastLocation().getY()
                || data.getPlayer().isFlying()
                && data.isTakingVelocity()
                || !PlayerUtils.isOnClimbable(data)
                || data.getLocation().getY() < data.getLastLocation().getY()
                || data.getDeltaY() != data.getLastDeltaY()
                || data.getPlayer().isInsideVehicle()) {
            return;
        }

        if (((float)data.getDeltaY()) > Config.CLIMB_SPEED) {
            if (++preVL > 3) {
                flag(data, "going up a ladder faster than possible. s: " + data.getDeltaY());
            }
        } else preVL = 0;
    }
}
