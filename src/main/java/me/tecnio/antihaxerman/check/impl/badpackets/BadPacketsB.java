package me.tecnio.antihaxerman.check.impl.badpackets;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.entityaction.WrappedPacketInEntityAction;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "BadPackets", type = "B")
public final class BadPacketsB extends Check {
    public BadPacketsB(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.ENTITY_ACTION) {
            final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(event.getNMSPacket());

            if (wrapper.getAction().name().contains("SPRINTING")) {
                if (increaseBuffer() > 1) {
                    flag();
                }
            }
        }
    }

    @Override
    public void onFlying() {
        setBuffer(0);
    }
}
