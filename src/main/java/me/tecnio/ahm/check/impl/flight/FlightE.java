package me.tecnio.ahm.check.impl.flight;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.api.enums.CheckState;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.impl.PositionTracker;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;
import me.tecnio.ahm.util.player.CustomLocation;

/**
 * Check to detect flight, speeds, long jumps and other invalid motions. (Also annoys Tecnio)
 */
@CheckManifest(name = "Flight", type = "E", description = "Checks the distance from the last ground location.", state = CheckState.DEVELOPMENT)
public class FlightE extends Check implements PositionCheck {

    public FlightE(final PlayerData data) {
        super(data);
    }

    private CustomLocation lastGround = null;
    private int airTicks = 0;

    private double rollingPredictionY = 0;

    @Override
    public void handle(final PositionUpdate update) {
        /*
            The idea behind this check is, when people are using fly they normally do not fall enough compared to the
            distance that they had traveled. So we can check for this by checking the distance from the player's
            last on ground location.
         */

        final PositionTracker position = data.getPositionTracker();
        final CustomLocation current = new CustomLocation(update.getX(), update.getY(), update.getZ());

        /*
            We check the current tick and the last tick just in case of random glitches where the player is on ground
            on the client side, but it would be detected as off server ground for 1 tick.
         */
        if (position.isServerGround() || position.isLastServerGround()) {
            lastGround = current;
            airTicks = 0;
            rollingPredictionY = 0.24786D;
            return;
        }

        final boolean exempt = this.isExempt(ExemptType.CLIMBABLE, ExemptType.PISTON, ExemptType.SLIME,
                ExemptType.VEHICLE, ExemptType.FLIGHT, ExemptType.TELEPORT,
                ExemptType.UNDER_BLOCK, ExemptType.WEB, ExemptType.LIQUID,
                ExemptType.TELEPORTED_RECENTLY, ExemptType.VELOCITY);

        // If they are exempted, then we need to wait until they are on ground again as it may cause falses if we don't.
        if (exempt) {
            lastGround = null;
        }

        // If the last on ground location has not been set yet we cant run the check.
        if (lastGround == null) return;

        airTicks++;

        final double distanceXZ = current.getVector().setY(0).distance(lastGround.getVector().setY(0));
        final double distanceY = current.getY() - lastGround.getY();

        double maxXZ = 1.7D;
        maxXZ += 0.3D * (airTicks - 5);

        if ((distanceXZ > maxXZ || distanceY > rollingPredictionY)) {
            if (this.buffer.increase() > 5) {
                fail("XZ: %s MaxXZ: %s Y: %s MaxY: %s", distanceXZ, maxXZ, distanceY, rollingPredictionY);
            }
        } else buffer.decreaseBy(0.125D);

        rollingPredictionY = airTicks > 5 ? (rollingPredictionY - 0.08D) * 0.9800000190734863D : rollingPredictionY;
    }
}