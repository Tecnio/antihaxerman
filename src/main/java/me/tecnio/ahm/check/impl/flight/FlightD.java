package me.tecnio.ahm.check.impl.flight;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.api.enums.CheckState;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;

@CheckManifest(name = "Flight", type = "D", description = "Checks for invalid fall motion.", state = CheckState.EXPERIMENTAL)
public class FlightD extends Check implements PositionCheck {

    public FlightD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        final double deltaY = update.getDeltaY();
        final double lastDeltaY = update.getLastDeltaY();

        final boolean ground = update.isOnGround();
        final boolean lastOnGround = update.isLastOnGround();

        final boolean startedFalling = deltaY <= 0.2 && !ground;

        final boolean invalid = !ground && !lastOnGround && deltaY >= lastDeltaY && startedFalling;
        final boolean exempt = this.isExempt(
                ExemptType.VEHICLE,
                ExemptType.VELOCITY,
                ExemptType.PISTON,
                ExemptType.CLIMBABLE,
                ExemptType.TELEPORT,
                ExemptType.BOAT,
                ExemptType.TELEPORTED_RECENTLY,
                ExemptType.UNDER_BLOCK,
                ExemptType.WEB,
                ExemptType.FLIGHT,
                ExemptType.SLIME,
                ExemptType.CHUNK,
                ExemptType.LIQUID,
                ExemptType.JOIN
        );

        if (invalid && !exempt) {
            if (this.buffer.increase() > 2) {
                this.fail("invalid fall motion");
            }
        } else {
            this.buffer.decreaseBy(0.125f);
        }
    }
}
