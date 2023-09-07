package me.tecnio.ahm.check.impl.flight;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;
import org.bukkit.potion.PotionEffectType;

/**
 * Check to detect invalid jump motion and unusual block placements that could indicate flight.
 */
@CheckManifest(name = "Flight", type = "B", description = "Detects invalid jump motion.")
public final class FlightB extends Check implements PositionCheck {

    public FlightB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        // The ground states of the current and last tick in order to detect if the player has jumped or gained velocity.
        final boolean onGround = update.isOnGround();
        final boolean lastOnGround = update.isLastOnGround();

        // The current motion upwards in order to be compared to the expected later on.
        final double deltaY = update.getDeltaY();

        // Calculate expected jump motion based on block placement ticks and potion effects
        final double modifierJump = data.getAttributeTracker().getPotionLevel(PotionEffectType.JUMP) * 0.1F;
        final double expectedJumpMotion = 0.42F + modifierJump;

        // This is the 0.03 compensated threshold that should prevent falses related to 0.03.
        final double threshold = (this.isExempt(ExemptType.RETARD) ? 0.03D : 0.0) + 1.0E-6;

        // Check exemption conditions
        final boolean exempt = this.isExempt(ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.PISTON,
                ExemptType.LIQUID, ExemptType.TELEPORT, ExemptType.WEB, ExemptType.BOAT, ExemptType.FLIGHT, ExemptType.SLIME,
                ExemptType.WALL, ExemptType.UNDER_BLOCK, ExemptType.CHUNK);

        // Check for invalid jump motion
        final boolean invalid = Math.abs(deltaY - expectedJumpMotion) > threshold && deltaY >= 0;

        // Checking for first "air tick" could be spoofed but they better jump this high if they spoof so idc
        if (!onGround && lastOnGround && invalid && !exempt) {
            this.fail("dY: %s", deltaY);
        }
    }
}