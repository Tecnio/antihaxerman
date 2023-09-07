package me.tecnio.ahm.processor.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.check.type.RotationCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.processor.PacketProcessor;
import me.tecnio.ahm.processor.prechecks.PreProcessorManager;

public final class IncomingPacketProcessor extends PacketProcessor {

    public IncomingPacketProcessor(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        for (final PreProcessorManager check : PreProcessorManager.values()) {
            if (check.getPrevention().handle(packet)) {
                data.haram();
                packet.setCancelled(true);

                return;
            }
        }

        if (packet instanceof PacketPlayClientFlying) {
            data.updateTicks();
        }

        this.handleProcessors(packet, false);

        for (final PacketCheck packetCheck : data.getPacketChecks()) {
            packetCheck.handle(packet);
        }

        if (packet instanceof PacketPlayClientFlying) {
            final PacketPlayClientFlying wrapper = ((PacketPlayClientFlying) packet);

            for (final PositionCheck positionCheck : data.getPositionChecks()) {
                positionCheck.handle(data.getPositionUpdate());
            }

            if (wrapper.isLook()) {
                for (final RotationCheck rotationCheck : data.getRotationChecks()) {
                    rotationCheck.handle(data.getRotationUpdate());
                }
            }
        }

        this.handleProcessors(packet, true);
    }
}
