package me.tecnio.antihaxerman.check.impl.aim;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

@CheckInfo(name = "Aim", type = "C")
public final class AimC extends Check {
    public AimC(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (PacketUtils.isRotationPacket(event.getPacketId())) {
            final float deltaPitch = data.getDeltaPitch();
            final float deltaYaw = data.getDeltaYaw();

            final boolean invalid = deltaPitch == 0.0F && deltaYaw > 1.5F;

            if (invalid) {
                if (increaseBuffer() > 7) {
                    flag();
                }
            } else {
                decreaseBufferBy(0.5);
            }
        }
    }
}
