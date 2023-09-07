package me.tecnio.ahm.data.tracker.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntity;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityDestroy;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityTeleport;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerSpawnNamedEntity;
import lombok.Getter;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.Tracker;
import me.tecnio.ahm.util.player.TrackerEntity;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class EntityTracker extends Tracker {

    private final Map<Integer, TrackerEntity> trackerMap = new HashMap<>();

    public EntityTracker(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayServerEntity) {
            final GPacketPlayServerEntity wrapper = ((GPacketPlayServerEntity) packet);
            final int id = wrapper.getEntityId();

            if (this.trackerMap.containsKey(id)) {
                final TrackerEntity trackerEntity = this.trackerMap.get(id);
                if (trackerEntity == null) return;

                data.getConnectionTracker().confirm(() -> {
                    final double x = wrapper.getX();
                    final double y = wrapper.getY();
                    final double z = wrapper.getZ();

                    trackerEntity.serverPosX += x;
                    trackerEntity.serverPosY += y;
                    trackerEntity.serverPosZ += z;

                    trackerEntity.setPosition2(
                            trackerEntity.serverPosX / 32.0D,
                            trackerEntity.serverPosY / 32.0D,
                            trackerEntity.serverPosZ / 32.0D
                    );
                });
            }
        } else if (packet instanceof GPacketPlayServerEntityTeleport) {
            final GPacketPlayServerEntityTeleport wrapper = ((GPacketPlayServerEntityTeleport) packet);
            final int id = wrapper.getEntityId();

            if (this.trackerMap.containsKey(id)) {
                final TrackerEntity trackerEntity = this.trackerMap.get(id);
                if (trackerEntity == null) return;

                data.getConnectionTracker().confirm(() -> {
                    trackerEntity.serverPosX = wrapper.getX();
                    trackerEntity.serverPosY = wrapper.getY();
                    trackerEntity.serverPosZ = wrapper.getZ();

                    final double d0 = (double) trackerEntity.serverPosX / 32.0D;
                    final double d1 = (double) trackerEntity.serverPosY / 32.0D;
                    final double d2 = (double) trackerEntity.serverPosZ / 32.0D;

                    final double deltaX = Math.abs(trackerEntity.getPosX() - d0);
                    final double deltaY = Math.abs(trackerEntity.getPosY() - d1);
                    final double deltaZ = Math.abs(trackerEntity.getPosZ() - d2);

                    if (deltaX < 0.03125D
                            && deltaY < 0.015625D
                            && deltaZ < 0.03125D) {
                        trackerEntity.setPosition2(trackerEntity.getPosX(), trackerEntity.getPosY(), trackerEntity.getPosZ());
                    } else {
                        trackerEntity.setPosition2(d0, d1, d2);
                    }
                });
            }
        } else if (packet instanceof GPacketPlayServerSpawnNamedEntity) {
            final GPacketPlayServerSpawnNamedEntity wrapper = ((GPacketPlayServerSpawnNamedEntity) packet);

            data.getConnectionTracker().confirm(() -> {
                final TrackerEntity trackerEntity = new TrackerEntity(wrapper.getEntityId(),
                        // Thank you artemis packet api for being shit and dividing my shit.
                        (int) Math.round(wrapper.getX() * 32.0D),
                        (int) Math.round(wrapper.getY() * 32.0D),
                        (int) Math.round(wrapper.getZ() * 32.0D)
                );
                trackerEntity.setPosition(wrapper.getX(), wrapper.getY(), wrapper.getZ());

                this.trackerMap.put(wrapper.getEntityId(), trackerEntity);
            });
        } else if (packet instanceof GPacketPlayServerEntityDestroy) {
            final GPacketPlayServerEntityDestroy wrapper = ((GPacketPlayServerEntityDestroy) packet);

            for (final int entity : wrapper.getEntities()) {
                this.trackerMap.remove(entity);
            }
        }
    }

    @Override
    public void handlePost(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            this.trackerMap.values().forEach(TrackerEntity::update);
        }
    }
}
