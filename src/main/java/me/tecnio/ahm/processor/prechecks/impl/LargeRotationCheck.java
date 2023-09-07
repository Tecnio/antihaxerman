package me.tecnio.ahm.processor.prechecks.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientLook;
import me.tecnio.ahm.processor.prechecks.PreProcessorCheck;

public final class LargeRotationCheck implements PreProcessorCheck {

    @Override
    public boolean handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            final PacketPlayClientFlying wrapper = ((PacketPlayClientFlying) packet);

            if (wrapper.isLook()) {
                final PacketPlayClientLook look = ((PacketPlayClientLook) packet);

                final float yaw = look.getYaw();
                final float pitch = look.getPitch();

                return Math.abs(yaw) >= 3.0E7
                        || Math.abs(pitch) >= 3.0E7
                        || !Float.isFinite(yaw)
                        || !Float.isFinite(pitch);
            }
        }


        return false;
    }
}
