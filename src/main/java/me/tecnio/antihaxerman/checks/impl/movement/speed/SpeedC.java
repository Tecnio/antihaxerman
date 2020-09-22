package me.tecnio.antihaxerman.checks.impl.movement.speed;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "Speed", type = "C")
public final class SpeedC extends Check {
    public SpeedC(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        double diff = data.getDeltaXZ() - data.getLastDeltaXZ();
        
        if (diff > PlayerUtils.getBaseSpeed(data.getPlayer())
                && !data.isTakingVelocity()
                && data.teleportTicks() > 20
                && !data.getPlayer().isInsideVehicle()
                && !data.getPlayer().isFlying()) {
            flag(data, "invalid acceleration, a: " + diff);
        }
    }
}
