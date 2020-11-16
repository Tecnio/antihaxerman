package me.tecnio.antihaxerman.check.impl.fastclimb;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "FastClimb", type = "A")
public final class FastClimbA extends Check {
    public FastClimbA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final boolean exempt = !data.isOnClimbable() || data.getLocation().getY() == data.getLastLocation().getY() || data.getPlayer().isFlying() || data.getLocation().getY() < data.getLastLocation().getY() || data.getDeltaY() != data.getLastDeltaY() || data.getPlayer().isInsideVehicle();

        if (((float) data.getDeltaY()) > 0.1176F && !exempt) {
            flag();
        }
    }
}
