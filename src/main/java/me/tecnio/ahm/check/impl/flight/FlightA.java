package me.tecnio.ahm.check.impl.flight;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;

/**
 * Check to detect vertical movement modifications indicative of flight cheats.
 */
@CheckManifest(name = "Flight", type = "A", description = "Detects for vertical movement modifications.")
public final class FlightA extends Check implements PositionCheck {

    public FlightA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        // Retrieve relevant data for analysis
        final boolean velocity = data.getVelocityTracker().getTicksSinceVelocity() == 1;
        final boolean lastVelocity = data.getVelocityTracker().isLastTickVelocity();

        final boolean ground = data.getPositionTracker().isOnGround();
        final boolean lastGround = data.getPositionTracker().isLastOnGround();

        final double lastLastDeltaY = data.getPositionTracker().getLastLastDeltaY();
        final double deltaY = data.getPositionTracker().getDeltaY();
        double lastDeltaY = data.getPositionTracker().getLastDeltaY();

        double distance = Double.MAX_VALUE;
        double predicted = 0.0D;

        for (final boolean chunk : new boolean[]{false, true}) {
            if (!data.getPositionTracker().isLastPosition() && !chunk) {
                lastDeltaY = (lastLastDeltaY - 0.08D) * 0.9800000190734863D;

                if (lastVelocity) lastDeltaY = data.getVelocityTracker().getLastVelocity().getY();
                if (Math.abs(lastDeltaY) < 0.005D) lastDeltaY = 0.0D;
            }

            final double bfMotion = chunk ? -0.1D : (lastDeltaY - 0.08D);
            double temp = bfMotion * 0.9800000190734863D;

            if (velocity) temp = data.getVelocityTracker().getVelocity().getY();
            if (Math.abs(temp) < 0.005D) temp = 0.0D;

            final double current = Math.abs(temp - deltaY);

            if (distance < current) {
                predicted = temp;
                distance = current;
            }
        }

        // Check for invalid conditions: significant difference between observed and predicted Y, not on ground, and previous tick not on ground.
        final boolean invalid = predicted > 1.0E-06 && !ground && !lastGround;
        final boolean exempt = this.isExempt(ExemptType.CLIMBABLE, ExemptType.PISTON, ExemptType.SLIME,
                ExemptType.VEHICLE, ExemptType.FLIGHT, ExemptType.TELEPORT, ExemptType.UNDER_BLOCK, ExemptType.WEB, ExemptType.LIQUID,
                ExemptType.TELEPORTED_RECENTLY);

        if (invalid && !exempt) {
            // Trigger a violation if the conditions are met and the buffer threshold is exceeded.
            if (this.buffer.increase() > 2) {
                this.fail("yD: %s", Math.abs(deltaY - predicted));
            }
        } else {
            // Decrease the buffer if the conditions are not met.
            this.buffer.decreaseBy(0.01D);
        }
    }
}