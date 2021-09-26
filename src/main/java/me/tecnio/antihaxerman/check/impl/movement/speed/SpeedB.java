

package me.tecnio.antihaxerman.check.impl.movement.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Speed", type = "B", description = "Checks for invalid friction on air.")
public final class SpeedB extends Check {
    public SpeedB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean sprinting = data.getActionProcessor().isSprinting();
            final int airTicks = data.getPositionProcessor().getAirTicks();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double predicted = (lastDeltaXZ * 0.91F) + (sprinting ? 0.026 : 0.02);
            final double difference = deltaXZ - predicted;

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.FLYING, ExemptType.VEHICLE, ExemptType.BOAT, ExemptType.UNDERBLOCK, ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE);
            final boolean invalid = difference > 1E-5 && predicted > 0.075 && airTicks > 2;

            if (invalid && !exempt) {
                if (increaseBuffer() > 5) {
                    fail();
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
