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
        if (this.isExempt(ExemptType.AIM)) return;

        final float deltaYaw = update.getDeltaYaw();
        final float deltaPitch = update.getDeltaPitch();

        if (deltaYaw < 1.0F || deltaPitch < 1.0F) return;

        final float divisorX = data.getRotationTracker().getDivisorX();
        final float divisorY = data.getRotationTracker().getDivisorY();

        final float divisorMax = Math.max(divisorX, divisorY);

        if (divisorMax < 0.0078125F) {
            if (this.buffer.increaseBy(0.3D) > 8) {
                this.fail("dM: " + divisorMax);
            }
        } else {
            this.buffer.decreaseBy(0.1D);
        }
    }
}
