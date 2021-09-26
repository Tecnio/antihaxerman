

package me.tecnio.antihaxerman.check.impl.combat.velocity;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Velocity", type = "A", description = "Checks for vertical velocity modifications.")
public final class VelocityA extends Check {
    public VelocityA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int ticksSinceVelocity = data.getVelocityProcessor().getTakingVelocityTicks();
            if (ticksSinceVelocity != 1) return;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double expectedDeltaY = data.getVelocityProcessor().getVelocityY();

            final double difference = Math.abs(deltaY - expectedDeltaY);
            final double percentage = (deltaY * 100.0) / expectedDeltaY;

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE, ExemptType.UNDERBLOCK, ExemptType.TELEPORT, ExemptType.FLYING);
            final boolean invalid = difference > 1E-10 && expectedDeltaY > 1E-2;

            if(isNegative(percentage)) {
                return;
            }
            if (invalid && !exempt) {
                if (increaseBuffer() > 3) {
                    fail(String.format("Velocity: %.2f%%", percentage));
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }

    public static boolean isNegative(double d) {
        return Double.compare(d, 0.0) < 0;
    }
}
