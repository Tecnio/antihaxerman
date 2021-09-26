

package me.tecnio.antihaxerman.check.impl.movement.largemove;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.potion.PotionEffect;

@CheckInfo(name = "LargeMove", type = "A", description = "Checks if the players horizontal movement is faster than possible.")
public final class LargeMoveA extends Check {
    public LargeMoveA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaXZ = Math.abs(data.getPositionProcessor().getDeltaXZ());

            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.JOINED, ExemptType.TELEPORT, ExemptType.TELEPORT_DELAY);
            final boolean invalid = deltaXZ > 30.0;

            for(PotionEffect pot : data.getPlayer().getActivePotionEffects()) {
                if(pot.getAmplifier() > 200){
                    return;
                }
            }
            if (invalid && !exempt) fail();
        }
    }
}
