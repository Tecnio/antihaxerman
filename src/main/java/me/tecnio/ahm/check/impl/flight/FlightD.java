package me.tecnio.ahm.check.impl.flight;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.api.enums.CheckState;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;

/**
 * Check to detect invalid fall motion that may indicate flight cheats.
 */
@CheckManifest(name = "Flight", type = "D", description = "Checks for invalid fall motion.", state = CheckState.EXPERIMENTAL)
public class FlightD extends Check implements PositionCheck {

    public FlightD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        /*
         * The main idea here is that when players start to fall, their motion cannot be higher than their previous motion
         * unless they are cheating. We check for this by comparing the current motion with the previous motion while the
         * player is in the air.
         */
        final double deltaY = update.getDeltaY();
        final double lastDeltaY = update.getLastDeltaY();

        final boolean ground = update.isOnGround();
        final boolean lastOnGround = update.isLastOnGround();

        final boolean exempt = this.isExempt(ExemptType.VEHICLE, ExemptType.VELOCITY, ExemptType.PISTON,
                ExemptType.CLIMBABLE, ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.TELEPORTED_RECENTLY,
                ExemptType.UNDER_BLOCK, ExemptType.WEB, ExemptType.FLIGHT, ExemptType.SLIME, ExemptType.CHUNK,
                ExemptType.LIQUID, ExemptType.JOIN);

        final boolean startedFalling = deltaY <= 0.2 && !ground;

        if (!ground && !lastOnGround && !exempt && deltaY >= lastDeltaY && startedFalling) {
            if (this.buffer.increase() > 2) {
                this.fail("invalid fall motion");
            }
        } else {
            this.buffer.decreaseBy(0.125f);
        }
    }
}
