package me.tecnio.ahm.check.impl.aura;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;
import org.bukkit.entity.Player;

/**
 * Check to detect keep-sprint behavior, commonly used in aura-like cheats.
 */
@CheckManifest(name = "Aura", type = "B", description = "Detects keep-sprint.")
public final class AuraB extends Check implements PositionCheck {

    public AuraB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        // Skip processing if the attack timer hasn't passed or the target is not a player.
        if (data.getActionTracker().getAttackTimer().hasPassed(1)
                || !(data.getActionTracker().getTarget() instanceof Player)) return;

        // Check for exemption conditions.
        final boolean exempt = this.isExempt(ExemptType.VEHICLE, ExemptType.CHUNK, ExemptType.FLIGHT, ExemptType.RETARD,
                ExemptType.PISTON, ExemptType.SLIME, ExemptType.LIQUID, ExemptType.TELEPORTED_RECENTLY);

        // Check for invalid conditions: sprinting without hit slowdown, sprinting, and being on the ground.
        final boolean invalid = !data.getEmulationTracker().isHitSlowdown()
                && data.getEmulationTracker().isSprint()
                && data.getPositionTracker().isOnGround();

        if (invalid && !exempt) {
            // Trigger a violation if the conditions are met and the buffer threshold is exceeded.
            if (this.buffer.increase() > 3) {
                this.fail();
            }
        } else {
            // Decrease the buffer if the conditions are not met.
            this.buffer.decreaseBy(0.1D);
        }
    }
}
