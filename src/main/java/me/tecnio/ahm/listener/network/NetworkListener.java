package me.tecnio.ahm.listener.network;

import ac.artemis.packet.PacketListener;
import ac.artemis.packet.profile.Profile;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.Packet;
import ac.artemis.packet.wrapper.PacketClient;
import cc.ghast.packet.wrapper.packet.play.client.*;
import cc.ghast.packet.wrapper.packet.play.server.*;
import me.tecnio.ahm.AHM;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.PlayerDataManager;

import java.util.Arrays;
import java.util.HashSet;

public final class NetworkListener implements PacketListener {

    private final HashSet<Class<?>> allowedPackets = new HashSet<>();

    public NetworkListener() {
        // I know this shit's ugly, but I really don't give a shit.
        this.allowedPackets.addAll(Arrays.asList(GPacketPlayClientFlying.class, GPacketPlayClientPosition.class,
                GPacketPlayClientPositionLook.class, GPacketPlayClientLook.class, GPacketPlayClientUseEntity.class,
                GPacketPlayClientKeepAlive.class, GPacketPlayServerKeepAlive.class, GPacketPlayClientTransaction.class,
                GPacketPlayServerTransaction.class, GPacketPlayServerEntity.class, GPacketPlayServerEntity.GPacketPlayServerEntityLook.class,
                GPacketPlayServerEntity.GPacketPlayServerRelEntityMove.class, GPacketPlayServerEntity.GPacketPlayServerRelEntityMoveLook.class,
                GPacketPlayServerEntityTeleport.class, GPacketPlayServerSpawnNamedEntity.class, GPacketPlayServerEntityDestroy.class,
                GPacketPlayClientBlockPlace.class, GPacketPlayClientArmAnimation.class, GPacketPlayClientBlockDig.class,
                GPacketPlayClientHeldItemSlot.class, GPacketPlayServerPosition.class, GPacketPlayServerBlockChange.class,
                GPacketPlayServerBlockChangeMulti.class, GPacketPlayClientEntityAction.class, GPacketPlayServerEntityEffect.class,
                GPacketPlayServerEntityEffectRemove.class, GPacketPlayServerUpdateAttributes.class, GPacketPlayServerEntityVelocity.class,
                GPacketPlayClientBlockDig.class
        ));
    }

    @Override
    public void onPacket(final Profile profile, final Packet packet) {
        if (profile == null || !this.allowedPackets.contains(packet.getClass())) return;

        final PlayerData data = AHM.get(PlayerDataManager.class).getPlayerData(profile.getUuid());

        if (data != null) {
            // TODO: 4/9/2023 switch to ahm executor
            if (packet instanceof PacketClient) data.getIncomingPacketProcessor().handle((GPacket) packet);
            else data.getOutgoingPacketProcessor().handle((GPacket) packet);
        }
    }
}
