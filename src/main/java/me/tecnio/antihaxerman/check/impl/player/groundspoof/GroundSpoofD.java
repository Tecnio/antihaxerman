

package me.tecnio.antihaxerman.check.impl.player.groundspoof;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "GroundSpoof", type = "D", description = "Checks for subtle ground modifications.")
public final class GroundSpoofD extends Check {
    public GroundSpoofD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isOnGround();
            final boolean inAir = data.getPositionProcessor().getAirTicks() > 5;
            final boolean mathGround = data.getPositionProcessor().isMathematicallyOnGround();

            final boolean exempt = isExempt(ExemptType.BUKKIT_PLACING, ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.WEB, ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CHUNK);
            final boolean invalid = onGround && inAir && !mathGround;

            if (invalid && !exempt && increaseBuffer() > 2) {
                fail();
            } else {
                decreaseBuffer();
            }
        }
    }
}
