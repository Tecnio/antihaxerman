package me.tecnio.ahm.check.impl.aim;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.RotationCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.RotationUpdate;

@CheckManifest(name = "Aim", type = "A", description = "Detects rotations with rounding flaws.")
public final class AimA extends Check implements RotationCheck {

    public AimA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final RotationUpdate update) {
        if (this.isExempt(ExemptType.AIM, ExemptType.TELEPORT)) return;

        final float constant = data.getRotationTracker().getSensitivity() / 142.0F;

        final float pitch = update.getPitch();
        final float yaw = update.getYaw();

        final float moduloPitch = Math.abs(pitch % constant);
        final float moduloYaw = Math.abs(yaw % constant);

        if (moduloPitch == 0.0D && moduloYaw == 0.0D) {
            if (this.buffer.increase() > 30) {
                this.fail("mP: " + moduloPitch);
            }
        } else {
            this.buffer.decreaseBy(0.05D);
        }
    }
}
