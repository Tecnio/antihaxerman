package me.tecnio.antihaxerman.checks.impl.movement.speed;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "Speed", type = "D")
public final class SpeedD extends Check {
    public SpeedD(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        double limit = data.sprintingTicks() < 20 ? PlayerUtils.getBaseSpeed(data.getPlayer(), 0.292F) : PlayerUtils.getBaseSpeed(data.getPlayer(), 0.23F);

        if (data.iceTicks() < 40 || data.slimeTicks() < 40) limit += 0.34;
        if (data.underBlockTicks() < 40) limit += 0.7;
        if (data.isTakingVelocity()) limit += Math.hypot(data.getLastVelocity().getX(), data.getLastVelocity().getZ());

        if (data.getGroundTicks() >= 15 && data.getDeltaXZ() > limit
                && !data.getPlayer().isFlying()
                && data.teleportTicks() > 40
                && data.getAirTicks() == 0) {
            if (++preVL > 1) {
                flag(data, "dist = " + data.getDeltaXZ());
            }
        }else preVL = 0;
    }
}
