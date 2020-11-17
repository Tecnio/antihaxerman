package me.tecnio.antihaxerman.check.impl.badpackets;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.abilities.WrappedPacketInAbilities;
import io.github.retrooper.packetevents.packetwrappers.out.abilities.WrappedPacketOutAbilities;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "BadPackets", type = "F")
public final class BadPacketsF extends Check {
    public BadPacketsF(PlayerData data) {
        super(data);
    }

    private boolean flightAllowed;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.ABILITIES) {
            final WrappedPacketInAbilities wrapper = new WrappedPacketInAbilities(event.getNMSPacket());

            final boolean invalid = wrapper.isFlying() && !flightAllowed;

            if (invalid) {
                flag();
            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketId() == PacketType.Server.ABILITIES) {
            final WrappedPacketOutAbilities wrapper = new WrappedPacketOutAbilities(event.getNMSPacket());

            flightAllowed = wrapper.isFlightAllowed();
        }
    }
}
