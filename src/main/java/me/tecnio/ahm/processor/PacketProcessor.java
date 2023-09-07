package me.tecnio.ahm.processor;

import ac.artemis.packet.spigot.wrappers.GPacket;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.Tracker;

public abstract class PacketProcessor {

    protected final PlayerData data;

    public PacketProcessor(final PlayerData data) {
        this.data = data;
    }

    public abstract void handle(final GPacket packet);

    protected final void handleProcessors(final GPacket packet, final boolean post) {
        for (final Tracker manager : data.getTrackers()) {
            if (post) {
                manager.handlePost(packet);
            } else {
                manager.handle(packet);
            }
        }
    }
}