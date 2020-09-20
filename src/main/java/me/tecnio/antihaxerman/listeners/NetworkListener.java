package me.tecnio.antihaxerman.listeners;

import io.github.retrooper.packetevents.annotations.PacketHandler;
import io.github.retrooper.packetevents.event.PacketListener;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.processors.PacketProcessor;
import me.tecnio.antihaxerman.processors.VelocityProcessor;

public final class NetworkListener implements PacketListener {

    @PacketHandler()
    public void onReceive(PacketReceiveEvent e) {
        PlayerData data = DataManager.INSTANCE.getUser(e.getPlayer().getUniqueId());
        if (data != null) {
            PacketProcessor.process(e);
            data.inbound(e);
            VelocityProcessor.processReceive(e);
        }
    }

    @PacketHandler
    public void onSend(PacketSendEvent e) {
        PlayerData data = DataManager.INSTANCE.getUser(e.getPlayer().getUniqueId());
        if (data != null) {
            data.outgoing(e);
            VelocityProcessor.processSend(e);
            if (e.getPacketId() == PacketType.Server.POSITION)data.setTeleportTicks(data.getTicks());
        }
    }
}
