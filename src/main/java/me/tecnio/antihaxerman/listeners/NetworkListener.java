package me.tecnio.antihaxerman.listeners;

import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.event.annotation.PacketHandler;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.manager.TransactionManager;

public final class NetworkListener implements PacketListener {
    @PacketHandler
    public void onPacketReceive(final PacketReceiveEvent event) {
        final PlayerData data = PlayerDataManager.getPlayerData().get(event.getPlayer().getUniqueId());
        if (data != null) {
            data.onPacketReceive(event);
        }

        TransactionManager.onPacketReceive(event);
    }

    @PacketHandler
    public void onPacketSend(final PacketSendEvent event) {
        final PlayerData data = PlayerDataManager.getPlayerData().get(event.getPlayer().getUniqueId());
        if (data != null) {
            data.onPacketSend(event);
        }

        TransactionManager.onPacketSend(event);
    }
}
