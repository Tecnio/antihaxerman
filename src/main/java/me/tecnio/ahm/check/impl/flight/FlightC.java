package me.tecnio.ahm.check.impl.flight;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;
import org.bukkit.potion.PotionEffectType;

/**
 * Check to detect invalid step motion that may indicate flight cheats.
 */
@CheckManifest(name = "Flight", type = "C", description = "Checks for invalid step motion.")
public class FlightC extends Check implements PositionCheck {

    public FlightC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        final boolean ground = update.isOnGround();

        // Calculate modified jump motion with potion effects
        final double modifierJump = data.getAttributeTracker().getPotionLevel(PotionEffectType.JUMP) * 0.1F;
        final double deltaY = update.getDeltaY() - modifierJump;

        // Check exemption conditions
        final boolean exempt = this.isExempt(ExemptType.VEHICLE, ExemptType.VELOCITY, ExemptType.PISTON,
                ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.FLIGHT, ExemptType.SLIME, ExemptType.CHUNK);

        /*
         * Check if their instant motion is more than possible while still on ground.
         * We use 0.6f to avoid false positives from fences/walls with carpets on top.
         */
        if (deltaY > 0.6f && ground && !exempt) {
            this.fail("dY: %s", deltaY);
        }
    }
}
