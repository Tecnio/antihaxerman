package me.tecnio.ahm.data.tracker.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityEffect;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityEffectRemove;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerUpdateAttributes;
import lombok.Getter;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.Tracker;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class AttributeTracker extends Tracker {

    private final Map<PotionEffectType, Integer> potions = new HashMap<>();

    private List<GPacketPlayServerUpdateAttributes.Snapshot> attributes = new ArrayList<>();

    public AttributeTracker(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayServerEntityEffect) {
            final GPacketPlayServerEntityEffect wrapper = ((GPacketPlayServerEntityEffect) packet);

            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                final PotionEffectType type = PotionEffectType.getById(wrapper.getEffectId());
                final int amplifier = wrapper.getAmplifier();

                data.getConnectionTracker().confirm(() -> this.potions.put(type, amplifier + 1));
            }
        } else if (packet instanceof GPacketPlayServerEntityEffectRemove) {
            final GPacketPlayServerEntityEffectRemove wrapper = ((GPacketPlayServerEntityEffectRemove) packet);

            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                final PotionEffectType type = PotionEffectType.getById(wrapper.getEffectId());

                data.getConnectionTracker().confirm(() -> this.potions.remove(type));
            }
        } else if (packet instanceof GPacketPlayServerUpdateAttributes) {
            final GPacketPlayServerUpdateAttributes wrapper = ((GPacketPlayServerUpdateAttributes) packet);

            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                data.getConnectionTracker().confirm(() -> this.attributes = wrapper.getAttributes());
            }
        }
    }

    public int getPotionLevel(final PotionEffectType type) {
        return this.potions.getOrDefault(type, 0);
    }

    public boolean hasPotion(final PotionEffectType type) {
        return this.potions.containsKey(type);
    }
}
