package me.tecnio.antihaxerman.checks.impl.movement.fastclimb;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "FastClimb", type = "A")
public class FastClimbA extends Check {
    @Override
    public void onMove(PlayerData data) {
        if (data.getLocation().getY() == data.getLastLocation().getY()
                || data.getPlayer().isFlying()
                && data.isTakingVelocity()
                || !PlayerUtils.isOnClimbable(data)
                || data.getLocation().getY() < data.getLastLocation().getY()
                || data.getDeltaY() != data.getLastDeltaY()
                || data.getPlayer().isInsideVehicle()) {
            return;
        }
        if (Math.abs(data.getLocation().getY() - data.getLastLocation().getY()) >= 0.12) {
            if (++preVL > 3) {
                flag(data, "going up a ladder faster than possible. s: " + data.getDeltaY());
            }
        } else preVL = 0;
    }
}
