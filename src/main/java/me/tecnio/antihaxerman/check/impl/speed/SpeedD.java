package me.tecnio.antihaxerman.check.impl.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;

@CheckInfo(name = "Speed", type = "D")
public final class SpeedD extends Check {
    public SpeedD(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        double limit = data.sprintingTicks() < 20 ? PlayerUtils.getBaseSpeed(data.getPlayer(), 0.305F) : PlayerUtils.getBaseSpeed(data.getPlayer(), 0.23F);

        if (data.iceTicks() < 40 || data.slimeTicks() < 40) limit += 0.34;
        if (data.collidedVTicks() < 40) limit += 0.7;
        if (data.isTakingVelocity()) limit += Math.hypot(data.getLastVelocity().getX(), data.getLastVelocity().getZ());

        final boolean exempt = data.getPlayer().isInsideVehicle() || data.flyingTicks() < 20 || data.pistonTicks() < 10 || data.teleportTicks() < 20;
        final boolean invalid = data.getGroundTicks() >= 15 && data.getDeltaXZ() > limit;

        if (invalid && !exempt) {
            if (increaseBuffer() > 2) {
                flag();
            }
        } else {
            resetBuffer();
        }
    }
}
