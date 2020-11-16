package me.tecnio.antihaxerman.check.impl.autoblock;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

@CheckInfo(name = "AutoBlock", type = "A")
public final class AutoBlockA extends Check {
    public AutoBlockA(PlayerData data) {
        super(data);
    }

    private boolean attacked;
    private int ticks;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.USE_ENTITY) {
            attacked = true;
        } else if (event.getPacketId() == PacketType.Client.BLOCK_PLACE) {
            if (attacked) {
                if (ticks < 2) {
                    if (increaseBuffer() > 2) {
                        flag();
                    }
                } else {
                    resetBuffer();
                }
                attacked = false;
            }

            ticks = 0;
        } else if (PacketUtils.isFlyingPacket(event.getPacketId())) {
            ticks++;
        }
    }
}
