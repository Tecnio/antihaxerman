package me.tecnio.ahm.check.impl.speed;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;
import me.tecnio.ahm.util.math.MathUtil;
import me.tecnio.ahm.util.player.PlayerUtil;

/**
 * Check to detect potential speed modifications.
 */
@CheckManifest(name = "Speed", type = "A", description = "Detects speed modifications.")
public final class SpeedA extends Check implements PositionCheck {

    private double motionX, motionZ;

    public SpeedA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        final boolean onGround = data.getPositionTracker().isOnGround();
        final boolean lastOnGround = data.getPositionTracker().isLastOnGround();

        final double deltaX = data.getPositionTracker().getDeltaX();
        final double deltaY = data.getPositionTracker().getDeltaY();
        final double deltaZ = data.getPositionTracker().getDeltaZ();

        final double deltaXZ = data.getPositionTracker().getDeltaXZ();

        float friction = 0.91F;
        if (lastOnGround) friction *= data.getPositionTracker().getSlipperiness();

        double movementSpeed = PlayerUtil.getAttributeSpeed(data, true);

        if (lastOnGround) {
            movementSpeed *= 0.16277136F / (friction * friction * friction);

            if (!onGround && deltaY >= 0.0D) {
                movementSpeed += 0.2D;
            }
        } else {
            movementSpeed = (float) ((double) 0.02F + (double) 0.02F * 0.3D);
        }

        if (data.getVelocityTracker().getTicksSinceVelocity() == 1) {
            this.motionX = data.getVelocityTracker().getVelocity().getX();
            this.motionZ = data.getVelocityTracker().getVelocity().getZ();
        }

        final double acceleration = (deltaXZ - (MathUtil.hypot(this.motionX, this.motionZ))) / movementSpeed;

        final boolean invalid = acceleration > 1.0D + 1.0E-6 && deltaXZ > 0.2D;
        final boolean exempt = this.isExempt(
                ExemptType.TELEPORTED_RECENTLY,
                ExemptType.PISTON,
                ExemptType.EXPLOSION,
                ExemptType.FLIGHT,
                ExemptType.CHUNK,
                ExemptType.SLOW,
                ExemptType.VEHICLE,
                ExemptType.LIQUID
        );

        if (invalid && !exempt) {
            final boolean certain = data.getVelocityTracker().getTicksSinceVelocity() > 2;
            final double increment = Math.min(certain ? 10.0D : 8.0D, Math.max(2.5D, acceleration));

            if (this.buffer.increaseBy(increment) > 8) {
                this.fail("a: " + acceleration);
            }
        } else {
            this.buffer.decreaseBy(0.02D);
        }

        this.motionX = deltaX * friction;
        this.motionZ = deltaZ * friction;

        if (Math.abs(this.motionX) < 0.005D) this.motionX = 0.0D;
        if (Math.abs(this.motionZ) < 0.005D) this.motionZ = 0.0D;
    }
}