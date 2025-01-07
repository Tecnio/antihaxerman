package me.tecnio.ahm.check.impl.velocity;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;

@CheckManifest(name = "Velocity", type = "A", description = "Detects for any horizontal velocity modification.")
public final class VelocityA extends Check implements PositionCheck {

    public VelocityA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        if (data.getVelocityTracker().getTicksSinceVelocity() > 8) return;

        final double distance = data.getEmulationTracker().getDistance();
        final double compensation = 0.03D * data.getPositionTracker().getDelayedFlyingTicks();

        final boolean invalid = distance > (this.isExempt(ExemptType.SLOW) ? compensation : 1.0E-07);
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
                ExemptType.SLOW,
                ExemptType.WALL,
                ExemptType.STEP,
                ExemptType.GHOST_WALL
        );

        if (invalid && !exempt) {
            if (this.buffer.increase() > 3) {
                this.fail("o: " + distance);
            }
        } else {
            this.buffer.decreaseBy(0.05D);
        }
    }
}