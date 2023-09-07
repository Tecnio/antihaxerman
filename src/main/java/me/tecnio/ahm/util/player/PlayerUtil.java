package me.tecnio.ahm.util.player;

import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerUpdateAttributes;
import lombok.experimental.UtilityClass;
import me.tecnio.ahm.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@UtilityClass
public final class PlayerUtil {

    public float getAttributeSpeed(PlayerData data, boolean sprinting) {
        double attributeSpeed = 0.1F;

        for (final GPacketPlayServerUpdateAttributes.Snapshot attribute : data.getAttributeTracker().getAttributes()) {
            if (attribute.getLocalName().equalsIgnoreCase("generic.movementspeed")) {
                attributeSpeed = attribute.getBaseValue();
            }
        }

        if (sprinting) {
            attributeSpeed += attributeSpeed * 0.30000001192092896D;
        }

        if (data.getAttributeTracker().getPotionLevel(PotionEffectType.SPEED) > 0) {
            attributeSpeed += data.getAttributeTracker().getPotionLevel(PotionEffectType.SPEED) * 0.20000000298023224D * attributeSpeed;
        }

        if (data.getAttributeTracker().getPotionLevel(PotionEffectType.SLOW) > 0) {
            attributeSpeed += data.getAttributeTracker().getPotionLevel(PotionEffectType.SLOW) * -0.15000000596046448D * attributeSpeed;
        }

        return (float) attributeSpeed;
    }

    /**
     * Bukkit's getNearbyEntities method looks for all entities in all chunks
     * This is a lighter method and can also be used Asynchronously since we won't load any chunks
     *
     * @param location The location to scan for nearby entities
     * @param radius   The radius to expand
     * @return The entities within that radius
     * @author Nik
     */
    public List<Entity> getEntitiesWithinRadius(final Location location, final double radius) {
        try {
            final double expander = 16.0D;

            final double x = location.getX();
            final double z = location.getZ();

            final int minX = (int) Math.floor((x - radius) / expander);
            final int maxX = (int) Math.floor((x + radius) / expander);

            final int minZ = (int) Math.floor((z - radius) / expander);
            final int maxZ = (int) Math.floor((z + radius) / expander);

            final World world = location.getWorld();

            final List<Entity> entities = new LinkedList<>();

            for (int xVal = minX; xVal <= maxX; xVal++) {

                for (int zVal = minZ; zVal <= maxZ; zVal++) {

                    if (!world.isChunkLoaded(xVal, zVal)) continue;

                    for (final Entity entity : world.getChunkAt(xVal, zVal).getEntities()) {
                        //We have to do this due to stupidness
                        if (entity == null) break;

                        //Make sure the entity is within the radius specified
                        if (entity.getLocation().distanceSquared(location) > radius * radius) continue;

                        entities.add(entity);
                    }
                }
            }

            return entities;
        } catch (final Throwable t) {
            // I know stfu
        }

        return new ArrayList<>();
    }
}
