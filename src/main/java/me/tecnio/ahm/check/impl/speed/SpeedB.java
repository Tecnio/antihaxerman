package me.tecnio.ahm.check.impl.speed;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;

/**
 * Check to detect horizontal movement modifications.
 */
@CheckManifest(name = "Speed", type = "B", description = "Checks for horizontal movement modifications.")
public final class SpeedB extends Check implements PositionCheck {

    public SpeedB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        if (!update.isPosition()) return;

        final double smallest = data.getEmulationTracker().getDistance();
        final double compensation = 0.03D * data.getPositionTracker().getDelayedFlyingTicks();

        final double max = data.getExemptTracker().isExempt(ExemptType.SLOW) ? compensation : 1.0E-14D;

        final boolean invalid = smallest > max;
        final boolean exempt = this.isExempt(
                ExemptType.JOIN,
                ExemptType.TELEPORTED_RECENTLY,
                ExemptType.PISTON,
                ExemptType.FLIGHT,
                ExemptType.LIQUID,
                ExemptType.SLIME,
                ExemptType.VEHICLE,
                ExemptType.CHUNK,
                ExemptType.CLIMBABLE,
                ExemptType.SOUL_SAND,
                ExemptType.WEB,
                ExemptType.ICE,
                ExemptType.WALL,
                ExemptType.STEP,
                ExemptType.SNEAK_EDGE
        );

        if (invalid && !exempt) {
            if (this.buffer.increase() > 20) {
                this.fail("o: " + smallest + " m: " + max);
            }
        } else {
            this.buffer.decreaseBy(0.25D);
        }
    }
}
