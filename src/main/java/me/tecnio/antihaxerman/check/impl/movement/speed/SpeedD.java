

package me.tecnio.antihaxerman.check.impl.movement.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "D", description = "Checks for invalid DeltaXZ.", experimental = true)
public final class SpeedD extends Check {
    public SpeedD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isFlying()) {
            double baseSpeed;

            if (data.getPositionProcessor().getClientAirTicks() > 0) {
                baseSpeed = 0.37 * Math.pow(0.99, Math.min(16, data.getPositionProcessor().getClientAirTicks()));
            } else {
                baseSpeed = 0.34 - (0.0055 * Math.min(9, data.getPositionProcessor().getGroundTicks()));
            }
            baseSpeed += PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) * (data.getPositionProcessor().isOnGround() ? 0.06f : 0.045f);
//                   baseSpeed *= data.hal > 0 ? 2.5 : 1;
//                   baseSpeed *= data.blockTicks > 0 ? 3.4 : 1;
            baseSpeed *= isExempt(ExemptType.NEARICE) && data.getPositionProcessor().getGroundTicks() < 6 ? 2.5f : 1.0;
            baseSpeed += data.getPositionProcessor().getSlimeTicks() > 0 ? 0.1 : 0;
            baseSpeed += isExempt(ExemptType.BUKKIT_PLACING) ? 0.1 : 0;
            baseSpeed += (data.getPlayer().getWalkSpeed() - 0.2) * 2.0f;
            if (data.getPositionProcessor().getDeltaXZ() > baseSpeed && !data.getVelocityProcessor().isTakingVelocity() && !isExempt(ExemptType.NEARSTAIRS, ExemptType.CREATIVE, ExemptType.FLYING)) {
                if (increaseBuffer() > 10) {
                    fail("DeltaXZ:" + MathUtil.round(data.getPositionProcessor().getDeltaXZ(), 4) + " BaseSpeed:" + MathUtil.round(baseSpeed, 4));
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
