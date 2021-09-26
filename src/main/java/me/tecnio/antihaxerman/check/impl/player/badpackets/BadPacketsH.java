

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "BadPackets", type = "H", description = "Speed bypass flaw detected.")
public final class BadPacketsH extends Check {
    public BadPacketsH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final int groundTicks = data.getPositionProcessor().getGroundTicks();
            final int airTicks = data.getPositionProcessor().getAirTicks();

            final boolean exempt = isExempt(ExemptType.SLIME);
            final boolean invalid = deltaY == 0.0 && groundTicks == 1 && airTicks == 0;

            if (invalid && !exempt) {
                if (increaseBuffer() > 8) {
                    fail();
                }
            } else {
                resetBuffer();
            }
        }
    }
}
