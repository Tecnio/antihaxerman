package me.tecnio.ahm.check.impl.flight;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;
import org.bukkit.potion.PotionEffectType;

@CheckManifest(name = "Flight", type = "C", description = "Checks for invalid step motion.")
public class FlightC extends Check implements PositionCheck {

    public FlightC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        final boolean ground = update.isOnGround();

        final double modifierJump = data.getAttributeTracker().getPotionLevel(PotionEffectType.JUMP) * 0.1F;
        final double deltaY = update.getDeltaY() - modifierJump;

        final boolean exempt = this.isExempt(
                ExemptType.VEHICLE,
                ExemptType.VELOCITY,
                ExemptType.PISTON,
                ExemptType.TELEPORT,
                ExemptType.BOAT,
                ExemptType.FLIGHT,
                ExemptType.SLIME,
                ExemptType.CHUNK
        );

        if (deltaY > 0.6f && ground && !exempt) {
            this.fail("dY: %s", deltaY);
        }
    }
}
