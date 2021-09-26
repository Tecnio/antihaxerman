

package me.tecnio.antihaxerman.check.impl.movement.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.potion.PotionEffect;

@CheckInfo(name = "Motion", type = "B", description = "Checks for terminal fall velocity.")
public final class MotionB extends Check {
    public MotionB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final boolean exempt = isExempt(ExemptType.CREATIVE, ExemptType.JOINED, ExemptType.TELEPORT, ExemptType.CHUNK);
            final boolean invalid = deltaY < -3.92;
            for(PotionEffect pot : data.getPlayer().getActivePotionEffects()) {
                if(pot.getAmplifier() > 200){
                    return;
                }
            }
            if (invalid && !exempt) {
                fail();
            }
        }
    }
}
