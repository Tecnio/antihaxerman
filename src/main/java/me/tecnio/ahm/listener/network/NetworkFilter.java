package me.tecnio.ahm.listener.network;

import cc.ghast.packet.wrapper.packet.play.client.*;
import cc.ghast.packet.wrapper.packet.play.server.*;

import java.util.Arrays;
import java.util.HashSet;

public final class NetworkFilter {

    private final HashSet<Class<?>> filters = new HashSet<>();

    public NetworkFilter() {
        this.filters.addAll(Arrays.asList(
                GPacketPlayClientFlying.class,
                GPacketPlayClientPosition.class,
                GPacketPlayClientPositionLook.class,
                GPacketPlayClientLook.class,
                GPacketPlayClientUseEntity.class,
                GPacketPlayClientKeepAlive.class,
                GPacketPlayServerKeepAlive.class,
                GPacketPlayClientTransaction.class,
                GPacketPlayServerTransaction.class,
                GPacketPlayServerEntity.class,
                GPacketPlayServerEntity.GPacketPlayServerEntityLook.class,
                GPacketPlayServerEntity.GPacketPlayServerRelEntityMove.class,
                GPacketPlayServerEntity.GPacketPlayServerRelEntityMoveLook.class,
                GPacketPlayServerEntityTeleport.class,
                GPacketPlayServerSpawnNamedEntity.class,
                GPacketPlayServerEntityDestroy.class,
                GPacketPlayClientBlockPlace.class,
                GPacketPlayClientArmAnimation.class,
                GPacketPlayClientBlockDig.class,
                GPacketPlayClientHeldItemSlot.class,
                GPacketPlayServerPosition.class,
                GPacketPlayServerBlockChange.class,
                GPacketPlayServerBlockChangeMulti.class,
                GPacketPlayClientEntityAction.class,
                GPacketPlayServerEntityEffect.class,
                GPacketPlayServerEntityEffectRemove.class,
                GPacketPlayServerUpdateAttributes.class,
                GPacketPlayServerEntityVelocity.class,
                GPacketPlayClientBlockDig.class
        ));
    }

    public boolean isAllowed(final Class<?> clazz) {
        return this.filters.contains(clazz);
    }
}
