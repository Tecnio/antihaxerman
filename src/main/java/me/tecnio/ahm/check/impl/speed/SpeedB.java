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

        // Get the smallest outcome
        final double smallest = data.getEmulationTracker().getDistance();
        // This value will determine a compensated value for 0.03 occurring
        final double compensation = 0.03D * data.getPositionTracker().getDelayedFlyingTicks();

        /*
         * We use this in order to set our max offset value this checks if a player is retarded(AKA 0.03 MOST LIKELY OCCURRED)
         * if so we compensate for it to prevent false positives, if not we don't have to worry about aids affecting.
         */
        final double max = data.getExemptTracker().isExempt(ExemptType.RETARD) ? compensation : 1.0E-14D;

        final boolean invalid = smallest > max;
        final boolean exempt = this.isExempt(ExemptType.JOIN, ExemptType.TELEPORTED_RECENTLY, ExemptType.PISTON,
                ExemptType.FLIGHT, ExemptType.LIQUID, ExemptType.SLIME, ExemptType.VEHICLE, ExemptType.CHUNK,
                ExemptType.CLIMBABLE, ExemptType.SOUL_SAND, ExemptType.WEB, ExemptType.ICE, ExemptType.WALL,
                ExemptType.STEP, ExemptType.SNEAK_EDGE
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
