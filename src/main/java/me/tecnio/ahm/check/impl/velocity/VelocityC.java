package me.tecnio.ahm.check.impl.velocity;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;

/**
 * Check to detect vertical velocity modifications.
 */
@CheckManifest(name = "Velocity", type = "C", description = "Checks for vertical velocity modifications.")
public final class VelocityC extends Check implements PositionCheck {

    private double velocity;

    public VelocityC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        // Check if recent velocity update
        if (data.getVelocityTracker().getTicksSinceVelocity() > 3) return;

        // If first tick after velocity update, store the initial velocity
        if (data.getVelocityTracker().getTicksSinceVelocity() == 1) {
            this.velocity = data.getVelocityTracker().getVelocity().getY();
        }

        // Get delta Y and calculate percentage change in Y velocity
        final double deltaY = data.getPositionTracker().getDeltaY();
        final float percentage = (float) (deltaY / this.velocity);

        // Check exemptions
        final boolean exempt = this.isExempt(ExemptType.JOIN, ExemptType.TELEPORTED_RECENTLY, ExemptType.PISTON,
                ExemptType.FLIGHT, ExemptType.LIQUID, ExemptType.SLIME, ExemptType.VEHICLE, ExemptType.CHUNK,
                ExemptType.CLIMBABLE, ExemptType.SOUL_SAND, ExemptType.WEB, ExemptType.ICE, ExemptType.UNDER_BLOCK,
                ExemptType.STEP) || update.isOnGround();

        // Check if percentage change is less than 1 and velocity is positive, and increase buffer if invalid
        if (percentage < 1 && this.velocity > 0 && !exempt) {
            if (this.buffer.increase() > 7) {
                this.fail("p: %s", (percentage * 100));
            }
        } else {
            this.buffer.decreaseBy(0.125);
        }

        // Update and decay the velocity
        this.velocity -= 0.08D;
        this.velocity *= 0.9800000190734863D;

        if (Math.abs(this.velocity) < 0.005D) this.velocity = 0.0;
    }
}