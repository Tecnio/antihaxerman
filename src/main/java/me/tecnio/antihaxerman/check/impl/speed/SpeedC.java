package me.tecnio.antihaxerman.check.impl.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;

@CheckInfo(name = "Speed", type = "C")
public final class SpeedC extends Check {
    public SpeedC(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        double limit = PlayerUtils.getBaseSpeed(data.getPlayer());

        if (data.iceTicks() < 40 || data.slimeTicks() < 40) limit += 0.34;
        if (data.collidedVTicks() < 40) limit += 0.91;
        if (data.isTakingVelocity()) limit += Math.hypot(data.getLastVelocity().getX(), data.getLastVelocity().getZ());

        final boolean exempt = data.getPlayer().isInsideVehicle() || data.pistonTicks() < 10 || data.flyingTicks() < 20 || data.teleportTicks() < 20;

        if (data.getDeltaXZ() > limit && !exempt) {
            if (increaseBuffer() > 7) {
                flag("breached limit, s: " + data.getDeltaXZ());
            }
        } else {
            setBuffer(buffer * 0.75);
        }
    }
}
