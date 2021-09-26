

package me.tecnio.antihaxerman.check.impl.movement.flight;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Flight", type = "C", description = "Checks for small vertical movement.")
public final class FlightC extends Check {
    public FlightC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = !data.getPositionProcessor().isInAir() || data.getPositionProcessor().isOnGround();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double difference = Math.abs(deltaY - lastDeltaY);

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.PISTON, ExemptType.VEHICLE, ExemptType.TELEPORT,
                    ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING, ExemptType.WEB, ExemptType.SLIME, ExemptType.VOID,
                    ExemptType.CLIMBABLE, ExemptType.CHUNK) || Math.abs(deltaY) > 3.0 || Math.abs(lastDeltaY) > 3.0;
            final boolean invalid = difference < 0.01 && !onGround && difference != 0.00;
            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
