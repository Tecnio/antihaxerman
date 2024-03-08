package me.tecnio.ahm.data.tracker.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientBlockPlace;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.Tracker;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.util.type.EvictingMap;

import java.util.Map;

/**
 * This is as basic as it gets this is just there to prevent any major bypass.
 */
public class GhostBlockTracker extends Tracker {

    private final Map<BlockPosition, Long> blockMap = new EvictingMap<>(10);
    private double buffer;

    public GhostBlockTracker(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            if (data.getExemptTracker().isExempt(ExemptType.CHUNK)) return;

            final boolean client = data.getPositionTracker().isOnGround();
            final boolean server = data.getPositionTracker().isServerGround();
            final boolean math = data.getPositionTracker().getY() % 0.015625D == 0.0D;

            handle: {
                if (!client || server || !math) break handle;

                boolean legit = false;

                for (final BlockPosition position : this.blockMap.keySet()) {
                    final boolean x = Math.floor(data.getPositionTracker().getX()) == position.getX();
                    // TODO: 9/7/23 fix artemis packet api insaneness
                    final boolean y = /*Math.floor(data.getPositionTracker().getY() - 0.03D) == position.getY()*/ true;
                    final boolean z = Math.floor(data.getPositionTracker().getZ()) == position.getZ();

                    legit |= x && y && z;
                }

                if (!legit) this.buffer++;
            }

            if (this.buffer > 2) {
                data.haram("Your blocks seems to be de-synced. Please rejoin.");
            }

            this.buffer = Math.max(this.buffer - 0.01D, 0.0D);
        }

        else if (packet instanceof PacketPlayClientBlockPlace) {
            final GPacketPlayClientBlockPlace wrapper = ((GPacketPlayClientBlockPlace) packet);

            this.blockMap.put(wrapper.getPosition(), packet.getTimestamp());
        }
    }
}
