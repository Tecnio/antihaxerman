package me.tecnio.antihaxerman.check.impl.flight;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Flight", type = "A")
public final class FlightA extends Check {
    public FlightA(PlayerData data) {
        super(data);
    }

    @Override
    public void onFlying() {
        final double predicted = (data.getLastDeltaY() - 0.08) * 0.9800000190734863;
        final double diff = Math.abs(data.getDeltaY() - predicted);

        final boolean exempt = data.getAirTicks() < 7 || data.isOnServerGround() || data.isTakingVelocity() || data.pistonTicks() < 10 || data.liquidTicks() < 10 || data.climbableTicks() < 10 || data.isNearBoat() || data.getPlayer().getVelocity().getY() >= -0.075D || data.flyingTicks() < 20 || data.getPlayer().isInsideVehicle() || data.isInWeb();

        if (diff > 0.001 && Math.abs(predicted) >= 0.005 && !exempt) {
            if (increaseBuffer() > 4) {
                flag();
            }
        } else {
            decreaseBufferBy(0.25);
        }
    }
}
