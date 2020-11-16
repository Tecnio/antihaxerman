package me.tecnio.antihaxerman.utils.packet;

import lombok.experimental.UtilityClass;

import static io.github.retrooper.packetevents.packettype.PacketType.Client;

@UtilityClass
public final class PacketUtils {
    public boolean isFlyingPacket(byte type) {
        return type == Client.FLYING || type == Client.POSITION || type == Client.POSITION_LOOK || type == Client.LOOK;
    }

    public boolean isPositionPacket(byte type) {
        return type == Client.POSITION || type == Client.POSITION_LOOK || type == Client.LOOK;
    }

    public boolean isRotationPacket(byte type) {
        return type == Client.POSITION_LOOK || type == Client.LOOK;
    }
}
