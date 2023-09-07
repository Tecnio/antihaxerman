package me.tecnio.ahm.check.type;

import ac.artemis.packet.spigot.wrappers.GPacket;

public interface PacketCheck {
    void handle(final GPacket packet);
}