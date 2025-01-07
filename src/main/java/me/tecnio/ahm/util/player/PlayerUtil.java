package me.tecnio.ahm.util.player;

import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerUpdateAttributes;
import lombok.experimental.UtilityClass;
import me.tecnio.ahm.data.PlayerData;
import org.bukkit.potion.PotionEffectType;

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
            attributeSpeed += data.getAttributeTracker()
                    .getPotionLevel(PotionEffectType.SPEED) * 0.20000000298023224D * attributeSpeed;
        }

        if (data.getAttributeTracker().getPotionLevel(PotionEffectType.SLOW) > 0) {
            attributeSpeed += data.getAttributeTracker()
                    .getPotionLevel(PotionEffectType.SLOW) * -0.15000000596046448D * attributeSpeed;
        }

        return (float) attributeSpeed;
    }
}
