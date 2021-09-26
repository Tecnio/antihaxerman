

package me.tecnio.antihaxerman.check.impl.player.groundspoof;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "GroundSpoof", type = "C", description = "Checks if player has vertical motion onGround.")
public final class GroundSpoofC extends Check {
    public GroundSpoofC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final int groundTicks = data.getPositionProcessor().getGroundTicks();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastY = data.getPositionProcessor().getLastY();

            final boolean step = deltaY % 0.015625 == 0.0 && lastY % 0.015625 == 0.0;

            final boolean exempt = isExempt(ExemptType.LONG_BUKKIT_PLACING, ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.WEB, ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CHUNK);
            final boolean invalid = groundTicks > 5 && deltaY != 0.0 && !step;

            if (invalid && !exempt) {
                if (increaseBuffer() > 1) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }
}
