package me.tecnio.ahm.check.impl.aim;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.RotationCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.RotationUpdate;

/**
 * Check to detect rotations with rounding flaws.
 */
@CheckManifest(name = "Aim", type = "A", description = "Detects rotations with rounding flaws.")
public final class AimA extends Check implements RotationCheck {

    public AimA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final RotationUpdate update) {
        // If the player is exempt from aim-related or teleportation-related checks, exit the method.
        if (this.isExempt(ExemptType.AIM, ExemptType.TELEPORT)) return;

        // Calculate the constant based on sensitivity to be used for calculations.
        final float constant = data.getRotationTracker().getSensitivity() / 142.0F;

        // Get the pitch and yaw values from the update.
        final float pitch = update.getPitch();
        final float yaw = update.getYaw();

        // Calculate the modulo of pitch and yaw with respect to the constant.
        final float moduloPitch = Math.abs(pitch % constant);
        final float moduloYaw = Math.abs(yaw % constant);

        // If both moduloPitch and moduloYaw are zero, increase the buffer count.
        if (moduloPitch == 0.0D && moduloYaw == 0.0D) {
            if (this.buffer.increase() > 30) {
                this.fail("mP: " + moduloPitch);
            }
        } else {
            // If at least one of the modulos is not zero, decrease the buffer by 0.05.
            this.buffer.decreaseBy(0.05D);
        }
    }
}
