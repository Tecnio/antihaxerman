

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "BadPackets", type = "I", description = "Checks for no position packet in 20 ticks.")
public final class BadPacketsI extends Check {

    private int streak;

    public BadPacketsI(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            if (wrapper.isPosition() || data.getPlayer().isInsideVehicle()) {
                streak = 0;
                return;
            }

            if (++streak > 20) {
                fail();
            }
        } else if (packet.isSteerVehicle()) {
            streak = 0;
        }
    }
}
