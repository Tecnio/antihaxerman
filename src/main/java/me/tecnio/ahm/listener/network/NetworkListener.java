package me.tecnio.ahm.listener.network;

import ac.artemis.packet.PacketListener;
import ac.artemis.packet.profile.Profile;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.Packet;
import ac.artemis.packet.wrapper.PacketClient;
import me.tecnio.ahm.AntiHaxerman;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.PlayerDataManager;

public final class NetworkListener implements PacketListener {

    private final NetworkFilter filter = new NetworkFilter();

    @Override
    public void onPacket(final Profile profile, final Packet packet) {
        if (profile == null || !this.filter.isAllowed(packet.getClass())) return;

        final PlayerData data = AntiHaxerman.get(PlayerDataManager.class).get(profile.getUuid());

        if (data == null) return;

        if (packet instanceof PacketClient) {
            data.getIncomingPacketProcessor().handle((GPacket) packet);
        } else {
            data.getOutgoingPacketProcessor().handle((GPacket) packet);
        }
    }
}
