

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.abilities.WrappedPacketInAbilities;

import java.util.Optional;

@CheckInfo(name = "BadPackets", type = "C", description = "Checks for fake abilities.")
public final class BadPacketsC extends Check {

    private Optional<Boolean> allowFlight;
    private boolean flying;

    public BadPacketsC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isOutgoingAbilities()) {
            final WrappedPacketInAbilities wrapper = new WrappedPacketInAbilities(packet.getRawPacket());

            allowFlight = wrapper.isFlightAllowed();
            flying = wrapper.isFlying();
        } else if (packet.isIncomingAbilities()) {
            final WrappedPacketInAbilities wrapper = new WrappedPacketInAbilities(packet.getRawPacket());

            final boolean flying = wrapper.isFlying();
            final Optional<Boolean> allowFlight = wrapper.isFlightAllowed();

            if (this.flying != flying || this.allowFlight != allowFlight) fail();
            if(!allowFlight.isPresent()) {
                return;
            }
            if (!allowFlight.get() && flying) fail();
        }
    }
}
