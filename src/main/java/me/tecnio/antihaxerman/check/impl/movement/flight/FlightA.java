

package me.tecnio.antihaxerman.check.impl.movement.flight;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Flight", type = "A", description = "Flags flight's that don't obey gravity.")
public final class FlightA extends Check {
    public FlightA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double velocityY = data.getVelocityProcessor().getVelocityY();

            final int clientAirTicks = data.getPositionProcessor().getClientAirTicks();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double predicted = (lastDeltaY - 0.08) * 0.9800000190734863;

            double fixedPredicted = Math.abs(predicted) < 0.005 ? 0.0 : predicted;
            if (isExempt(ExemptType.VELOCITY_ON_TICK)) fixedPredicted = velocityY;

            final double difference = Math.abs(deltaY - fixedPredicted);

            final boolean exempt = isExempt(ExemptType.BUKKIT_PLACING, ExemptType.VELOCITY, ExemptType.PISTON, ExemptType.VEHICLE, ExemptType.TELEPORT,
                    ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING, ExemptType.WEB, ExemptType.JOINED,
                    ExemptType.SLIME, ExemptType.CLIMBABLE, ExemptType.CHUNK, ExemptType.VOID, ExemptType.UNDERBLOCK,
                    ExemptType.VELOCITY_ON_TICK);
            final boolean invalid = difference > 1E-8 && (clientAirTicks > 1 && data.getPositionProcessor().getSinceTeleportTicks() > 2
                    || data.getPositionProcessor().getAirTicks() > 2 && !isExempt(ExemptType.GHOST_BLOCK));

            debug(difference + " CAT: " + clientAirTicks + " SAT: " + data.getPositionProcessor().getAirTicks());
            if (invalid && !exempt && !String.format("%.4f", fixedPredicted).equals("-0.1744") && !String.format("%.4f", deltaY).equals("-0.0980")) {
                if (increaseBuffer() > 3) {
                    fail(String.format("pred: %.4f delta: %.4f vel: %s", fixedPredicted, deltaY, isExempt(ExemptType.VELOCITY_ON_TICK)));
                }
            } else {
                decreaseBufferBy(0.15);
            }
        }
    }
}
