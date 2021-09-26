

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "BadPackets", type = "N", description = "Checks for disablers.")
public final class BadPacketsN extends Check {
    public BadPacketsN(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isFlying()) {
            WrappedPacketInFlying wrapped = new WrappedPacketInFlying(packet.getRawPacket());
            if (wrapped.getYaw() > 1200.0f && (wrapped.getYaw() % 360.0f > 1200.0f)) {
                fail("deltaYaw: " + wrapped.getYaw() );
            }
        }
    }
}
