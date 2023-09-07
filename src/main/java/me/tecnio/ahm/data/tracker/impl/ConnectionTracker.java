package me.tecnio.ahm.data.tracker.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientKeepAlive;
import ac.artemis.packet.wrapper.client.PacketPlayClientTransaction;
import ac.artemis.packet.wrapper.server.PacketPlayServerKeepAlive;
import ac.artemis.packet.wrapper.server.PacketPlayServerTransaction;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerTransaction;
import lombok.Getter;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.Tracker;
import me.tecnio.ahm.util.type.EvictingMap;

import java.util.Optional;

@Getter
public final class ConnectionTracker extends Tracker {

    private final EvictingMap<Long, Long> keepAliveMap = new EvictingMap<>(800);
    private final EvictingMap<Short, Long> transactionMap = new EvictingMap<>(800);

    private final EvictingMap<Short, Runnable> confirmationMap = new EvictingMap<>(800);

    private int transactionPing, keepAlivePing;
    private short transactionId = Short.MIN_VALUE;
    private int keepAliveId = Short.MIN_VALUE;

    private long lastFlying, flyingDelay;
    private long lastKeepAlive = System.currentTimeMillis(), lastTransaction = System.currentTimeMillis();

    private int ticksSinceLag;

    public ConnectionTracker(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayServerKeepAlive) {
            final PacketPlayServerKeepAlive wrapper = ((PacketPlayServerKeepAlive) packet);

            this.keepAliveMap.put(wrapper.getId(), packet.getTimestamp());
        } else if (packet instanceof PacketPlayServerTransaction) {
            final GPacketPlayServerTransaction wrapper = ((GPacketPlayServerTransaction) packet);

            this.transactionMap.put(wrapper.getActionNumber(), packet.getTimestamp());
        } else if (packet instanceof PacketPlayClientKeepAlive) {
            final PacketPlayClientKeepAlive wrapper = ((PacketPlayClientKeepAlive) packet);

            this.keepAliveMap.computeIfPresent(wrapper.getId(), (id, time) -> {
                this.keepAlivePing = (int) (packet.getTimestamp() - time);
                this.lastKeepAlive = packet.getTimestamp();

                this.keepAliveMap.remove(wrapper.getId());

                return time;
            });
        } else if (packet instanceof PacketPlayClientTransaction) {
            final PacketPlayClientTransaction wrapper = ((PacketPlayClientTransaction) packet);

            this.transactionMap.computeIfPresent(wrapper.getActionNumber(), (id, time) -> {
                this.transactionPing = (int) (packet.getTimestamp() - time);
                this.lastTransaction = packet.getTimestamp();

                this.transactionMap.remove(wrapper.getActionNumber());

                return time;
            });

            Optional.ofNullable(this.confirmationMap.remove(wrapper.getActionNumber()))
                    .ifPresent(Runnable::run);
        } else if (packet instanceof PacketPlayClientFlying) {
            final long now = packet.getTimestamp();

            this.flyingDelay = now - this.lastFlying;
            this.lastFlying = now;

            if (this.flyingDelay < 30 || this.flyingDelay > 70) this.ticksSinceLag = 0;
        }
    }

    public void confirm(final Runnable runnable) {
        this.transactionId = this.transactionId > 0 ? Short.MIN_VALUE : (short) (this.transactionId + 1);
        this.confirmationMap.put(this.transactionId, runnable);

        data.send(new GPacketPlayServerTransaction((byte) 0, this.transactionId, false));
    }
}
