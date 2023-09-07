package me.tecnio.ahm.check.impl.velocity;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;

/**
 * Check to detect any horizontal velocity modification.
 */
@CheckManifest(name = "Velocity", type = "A", description = "Detects for any horizontal velocity modification.")
public final class VelocityA extends Check implements PositionCheck {

    public VelocityA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        // Check if recent velocity update
        if (data.getVelocityTracker().getTicksSinceVelocity() > 8) return;

        // Calculate the distance and compensation for delayed flying ticks
        final double distance = data.getEmulationTracker().getDistance();
        final double compensation = 0.03D * data.getPositionTracker().getDelayedFlyingTicks();

        // Check if the distance is invalid and handle exemptions
        final boolean invalid = distance > (this.isExempt(ExemptType.RETARD) ? compensation : 1.0E-07);
        final boolean exempt = this.isExempt(ExemptType.JOIN, ExemptType.TELEPORTED_RECENTLY, ExemptType.PISTON,
                ExemptType.FLIGHT, ExemptType.LIQUID, ExemptType.SLIME, ExemptType.VEHICLE, ExemptType.CHUNK,
                ExemptType.CLIMBABLE, ExemptType.SOUL_SAND, ExemptType.WEB, ExemptType.ICE, ExemptType.RETARD,
                ExemptType.WALL, ExemptType.STEP, ExemptType.GHOST_WALL);

        // Check if invalid and increase buffer, or decrease buffer if valid
        if (invalid && !exempt) {
            if (this.buffer.increase() > 3) {
                this.fail("o: " + distance);
            }
        } else {
            this.buffer.decreaseBy(0.05D);
        }
    }
}