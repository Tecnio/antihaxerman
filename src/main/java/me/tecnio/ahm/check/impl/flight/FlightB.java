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
        final boolean onGround = update.isOnGround();
        final boolean lastOnGround = update.isLastOnGround();

        final double deltaY = update.getDeltaY();

        final double modifierJump = data.getAttributeTracker().getPotionLevel(PotionEffectType.JUMP) * 0.1F;
        final double expectedJumpMotion = 0.42F + modifierJump;

        final double threshold = (this.isExempt(ExemptType.SLOW) ? 0.03D : 0.0) + 1.0E-6;
        
        final boolean invalid = Math.abs(deltaY - expectedJumpMotion) > threshold && deltaY >= 0;
        final boolean exempt = this.isExempt(
                ExemptType.VEHICLE,
                ExemptType.CLIMBABLE,
                ExemptType.VELOCITY,
                ExemptType.PISTON,
                ExemptType.LIQUID,
                ExemptType.TELEPORT,
                ExemptType.WEB,
                ExemptType.BOAT,
                ExemptType.FLIGHT,
                ExemptType.SLIME,
                ExemptType.WALL,
                ExemptType.UNDER_BLOCK,
                ExemptType.CHUNK
        );
        
        if (!onGround && lastOnGround && invalid && !exempt) {
            this.fail("dY: %s", deltaY);
        }
    }
}