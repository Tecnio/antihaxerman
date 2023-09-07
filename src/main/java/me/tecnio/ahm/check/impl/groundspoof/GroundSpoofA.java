package me.tecnio.ahm.check.impl.groundspoof;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.PositionUpdate;

@CheckManifest(name = "GroundSpoof", type = "A", description = "Checks for ground spoof.")
public final class GroundSpoofA extends Check implements PositionCheck {

    public GroundSpoofA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final PositionUpdate update) {
        if (this.isExempt(ExemptType.CHUNK, ExemptType.CLIMBABLE, ExemptType.SLIME, ExemptType.BOAT, ExemptType.RETARD)) return;

        final boolean ground = data.getPositionTracker().isOnGround();
        final boolean mathGround = data.getPositionTracker().getY() % 0.015625D == 0.0D;
        final boolean serverGround = data.getPositionTracker().isServerGround();

        if (ground && !mathGround && !serverGround) {
            if (this.buffer.increase() > 1) {
                this.fail();
            }
        } else {
            this.buffer.decreaseBy(0.001D);
        }
    }
}