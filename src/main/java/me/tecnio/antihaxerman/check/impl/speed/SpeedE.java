package me.tecnio.antihaxerman.check.impl.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;

@CheckInfo(name = "Speed", type = "E")
public final class SpeedE extends Check {
    public SpeedE(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final double diff = data.getDeltaXZ() - data.getLastDeltaXZ();

        final boolean exempt = data.flyingTicks() < 40 || data.teleportTicks() < 20 || data.pistonTicks() < 20 || data.isTakingVelocity();

        if (diff > PlayerUtils.getBaseSpeed(data.getPlayer()) && !exempt) {
            flag();
        }
    }
}
