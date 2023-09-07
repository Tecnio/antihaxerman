package me.tecnio.ahm.check.impl.aim;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.RotationCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.RotationUpdate;

/**
 * Check to detect rotations without a rotation constant.
 */
@CheckManifest(name = "Aim", type = "B", description = "Detects rotations without a constant.")
public final class AimB extends Check implements RotationCheck {

    public AimB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final RotationUpdate update) {
        // If the player is exempt from aim-related checks, exit the method.
        if (this.isExempt(ExemptType.AIM)) return;

        // Get the changes in yaw and pitch from the update.
        final float deltaYaw = update.getDeltaYaw();
        final float deltaPitch = update.getDeltaPitch();

        // If either the change in yaw or pitch is too small, exit the method as we can't do the math
        // as we require both yaw and the pitch to be moving in order to get the highest GCD between them.
        if (deltaYaw < 1.0F || deltaPitch < 1.0F) return;

        // Get the divisor values for the x and y axes from the rotation tracker.
        final float divisorX = data.getRotationTracker().getDivisorX();
        final float divisorY = data.getRotationTracker().getDivisorY();

        // Find the maximum divisor value between the x and y axes.
        final float divisorMax = Math.max(divisorX, divisorY);

        // If the maximum divisor is below a certain threshold, increase the buffer count.
        if (divisorMax < 0.0078125F) {
            if (this.buffer.increaseBy(0.3D) > 8) {
                this.fail("dM: " + divisorMax);
            }
        } else {
            // If the maximum divisor is not too small, decrease the buffer by 0.1.
            this.buffer.decreaseBy(0.1D);
        }
    }
}
