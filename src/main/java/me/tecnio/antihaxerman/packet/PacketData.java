package me.tecnio.antihaxerman.packet;

import io.github.retrooper.packetevents.utils.vector.Vector3d;
import lombok.Getter;
import lombok.Setter;

public class PacketData{

    @Setter@Getter
    Vector3d vector;
    @Setter@Getter
    float yaw;
    @Setter@Getter
    float pitch;

    public PacketData(Vector3d vector, float yaw, float pitch) {
        this.vector = vector;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
