package me.tecnio.ahm.check.impl.aura;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;
import org.bukkit.entity.Player;

@CheckManifest(name = "Aura", type = "B", description = "Detects keep-sprint.")
public final class AuraB extends Check implements PositionCheck {

    public AuraB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        if (data.getActionTracker().getAttackTimer().hasPassed(1)
                || !(data.getActionTracker().getTarget() instanceof Player)) return;

        final boolean exempt = this.isExempt(
                ExemptType.VEHICLE,
                ExemptType.CHUNK,
                ExemptType.FLIGHT,
                ExemptType.SLOW,
                ExemptType.PISTON,
                ExemptType.SLIME,
                ExemptType.LIQUID,
                ExemptType.TELEPORTED_RECENTLY
        );

        final boolean invalid = !data.getEmulationTracker().isHitSlowdown()
                && data.getEmulationTracker().isSprint()
                && data.getPositionTracker().isOnGround();

        if (invalid && !exempt) {
            if (this.buffer.increase() > 3) {
                this.fail();
            }
        } else {
            this.buffer.decreaseBy(0.1D);
        }
    }
}
