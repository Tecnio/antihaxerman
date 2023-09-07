package me.tecnio.ahm.processor.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.processor.PacketProcessor;

public final class OutgoingPacketProcessor extends PacketProcessor {

    public OutgoingPacketProcessor(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        this.handleProcessors(packet, false);

        for (final PacketCheck packetCheck : data.getPacketChecks()) {
            packetCheck.handle(packet);
        }

        this.handleProcessors(packet, true);
    }
}
