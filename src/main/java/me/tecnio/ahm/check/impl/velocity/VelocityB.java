package me.tecnio.ahm.check.impl.velocity;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;
import org.bukkit.potion.PotionEffectType;

/**
 * Check to detect vertical velocity modifications.
 */
@CheckManifest(name = "Velocity", type = "B", description = "Checks for vertical velocity modifications.")
public final class VelocityB extends Check implements PositionCheck {

    public VelocityB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        // Check if recent velocity update
        if (data.getVelocityTracker().getTicksSinceVelocity() != 1) return;

        // Calculate expected jump motion
        final float expectedJumpMotion = 0.42F + (data.getAttributeTracker().getPotionLevel(PotionEffectType.JUMP) * 0.1F);

        // Get delta Y and velocity Y
        final double deltaY = data.getPositionTracker().getDeltaY();
        final double velocityY = data.getVelocityTracker().getVelocity().getY();

        // Calculate offsets
        final double offset = Math.abs(deltaY - velocityY);
        final double jumpOffset = Math.abs(deltaY - expectedJumpMotion);

        // Calculate compensation and maximum threshold
        final double compensation = 0.03D * data.getPositionTracker().getDelayedFlyingTicks();
        final double max = (this.isExempt(ExemptType.RETARD) ? compensation : 1.0E-08);

        // Check primary and secondary conditions for invalidity
        final boolean primary = offset > max && jumpOffset > 1.0E-08;
        final boolean secondary = offset > max && jumpOffset > max && data.getEmulationTracker().isJump();

        // Check for invalid vertical velocity modification
        final boolean invalid = (primary || secondary) && velocityY > 0.05;
        final boolean exempt = this.isExempt(ExemptType.JOIN, ExemptType.TELEPORTED_RECENTLY, ExemptType.PISTON,
                ExemptType.FLIGHT, ExemptType.LIQUID, ExemptType.SLIME, ExemptType.VEHICLE, ExemptType.CHUNK,
                ExemptType.CLIMBABLE, ExemptType.SOUL_SAND, ExemptType.WEB, ExemptType.ICE, ExemptType.UNDER_BLOCK,
                ExemptType.STEP);

        // Check if invalid and increase buffer, or decrease buffer if valid
        if (invalid && !exempt) {
            if (this.buffer.increase() > 2) {
                this.fail();
            }
        } else {
            this.buffer.decreaseBy(0.1D);
        }
    }
}