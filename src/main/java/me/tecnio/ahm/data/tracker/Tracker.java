package me.tecnio.ahm.data.tracker;

import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.RequiredArgsConstructor;
import me.tecnio.ahm.data.PlayerData;

@RequiredArgsConstructor
public abstract class Tracker {

    protected final PlayerData data;

    public void handle(final GPacket packet) {
    }

    public void handlePost(final GPacket packet) {
    }
}
