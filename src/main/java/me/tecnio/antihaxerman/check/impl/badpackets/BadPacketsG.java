package me.tecnio.antihaxerman.check.impl.badpackets;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

@CheckInfo(name = "BadPackets", type = "G")
public final class BadPacketsG extends Check {
    public BadPacketsG(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.FLYING) {
            increaseBuffer();
        } else if (PacketUtils.isPositionPacket(event.getPacketId())) {
            if (getBuffer() > 20) {
                flag();
            }

            resetBuffer();
        }
    }
}
