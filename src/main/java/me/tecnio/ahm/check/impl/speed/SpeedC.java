package me.tecnio.ahm.check.impl.speed;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;

/**
 * Check to detect invalid acceleration.
 */
@CheckManifest(name = "Speed", type = "C", description = "Checks for invalid acceleration.")
public final class SpeedC extends Check implements PositionCheck {

    public SpeedC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        if (!update.isPosition()) return;

        final double deltaXZ = data.getPositionTracker().getDeltaXZ();
        final double accel = data.getPositionTracker().getAcceleration();

        if (Math.abs(accel - deltaXZ) < 0.0001 && deltaXZ > 0.1) {
            if (this.buffer.increase() > 2) {
                fail("Accel: %s DeltaXZ: %s", accel, deltaXZ);
            }
        } else this.buffer.decreaseBy(0.2f);
    }
}
