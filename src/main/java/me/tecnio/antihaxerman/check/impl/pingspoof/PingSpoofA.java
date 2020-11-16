package me.tecnio.antihaxerman.check.impl.pingspoof;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.out.keepalive.WrappedPacketOutKeepAlive;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

import java.util.HashMap;
import java.util.Map;

@CheckInfo(name = "PingSpoof" , type = "A", autoBan = false)
public final class PingSpoofA extends Check {
    public PingSpoofA(PlayerData data) {
        super(data);
    }

    private final Map<Long, Long> keepAliveUpdates = new HashMap<>();
    private int keepAlivePing;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.KEEP_ALIVE) {
            final WrappedPacketInKeepAlive wrapper = new WrappedPacketInKeepAlive(event.getNMSPacket());

            final long now = System.currentTimeMillis();

            keepAliveUpdates.computeIfPresent(wrapper.getId(), (id, time) -> {
                keepAlivePing = (int) (now - time);
                keepAliveUpdates.remove(id);

                return time;
            });

            final int transactionPing = data.getTransactionPing();
            final int keepAlivePing = this.keepAlivePing;

            final int diff = Math.abs(transactionPing - keepAlivePing);

            final boolean exempt = data.isLagging() || data.getTick() < 100 || data.teleportTicks() < 100 || !data.getPlayer().getLocation().getChunk().isLoaded() || PacketEvents.getAPI().getServerUtils().getTPS() < 18;

            if (diff > 100 && !exempt) {
                if (increaseBuffer() > 30) {
                    flag();
                }
            } else {
                decreaseBufferBy(5);
            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketId() == PacketType.Server.KEEP_ALIVE) {
            final WrappedPacketOutKeepAlive wrapper = new WrappedPacketOutKeepAlive(event.getNMSPacket());

            keepAliveUpdates.put(wrapper.getId(), System.currentTimeMillis());
        }
    }
}
