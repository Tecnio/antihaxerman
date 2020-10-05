package me.tecnio.antihaxerman.checks.impl.movement.speed;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "Speed", type = "B")
public final class SpeedB extends Check {
    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        double limit = PlayerUtils.getBaseSpeed(data.getPlayer());

        if (elapsed(data.getTicks(), data.getIceTicks()) < 40 || elapsed(data.getTicks(), data.getSlimeTicks()) < 40) limit += 0.34;
        if (elapsed(data.getTicks(), data.getUnderBlockTicks()) < 40) limit += 0.91;
        if (data.isTakingVelocity()) limit += Math.hypot(Math.abs(data.getLastVelocity().getX()), Math.abs(data.getLastVelocity().getZ()));;

        if (data.getDeltaXZ() > limit
                && !data.getPlayer().isInsideVehicle()
                && !data.getPlayer().isFlying()
                && data.teleportTicks() > 10) {
            if (++buffer > 6) {
                flag(data, "breached limit, s: " + data.getDeltaXZ());
            }
        } else buffer *= 0.75;
    }


}
