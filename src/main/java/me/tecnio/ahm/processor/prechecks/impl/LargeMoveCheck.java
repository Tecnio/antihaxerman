package me.tecnio.ahm.processor.prechecks.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientPosition;
import me.tecnio.ahm.processor.prechecks.PreProcessorCheck;

public final class LargeMoveCheck implements PreProcessorCheck {

    @Override
    public boolean handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            final PacketPlayClientFlying wrapper = ((PacketPlayClientFlying) packet);

            if (wrapper.isPos()) {
                final PacketPlayClientPosition position = ((PacketPlayClientPosition) wrapper);

                final double x = position.getX();
                final double y = position.getY();
                final double z = position.getZ();

                return Math.abs(x) >= 3.0E7
                        || Math.abs(y) >= 3.0E7
                        || Math.abs(z) >= 3.0E7
                        || !Double.isFinite(x)
                        || !Double.isFinite(y)
                        || !Double.isFinite(z);
            }
        }

        return false;
    }
}
