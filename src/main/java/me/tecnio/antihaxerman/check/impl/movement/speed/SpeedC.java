

package me.tecnio.antihaxerman.check.impl.movement.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "C", description = "Checks if player is going faster than possible")
public final class SpeedC extends Check {
    public SpeedC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if(data.getPlayer().getWalkSpeed() <= 0.2 || data.getConnectionProcessor().getTransactionPing() == 0) {
                return;
            }
            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final int groundTicks = data.getPositionProcessor().getGroundTicks();
            final int airTicks = data.getPositionProcessor().getClientAirTicks();

            final float modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
            final float jumpMotion = 0.42F + modifierJump;

            double groundLimit = PlayerUtil.getBaseGroundSpeed(data.getPlayer());
            double airLimit = PlayerUtil.getBaseSpeed(data.getPlayer());


            if (Math.abs(deltaY - jumpMotion) < 1.0E-4 && airTicks == 1 && sprinting) {
                final float f = data.getRotationProcessor().getYaw() * 0.017453292F;

                final double x = lastDeltaX - (Math.sin(f) * 0.2F);
                final double z = lastDeltaZ + (Math.cos(f) * 0.2F);

                airLimit += Math.hypot(x, z);
            }

            if (isExempt(ExemptType.ICE, ExemptType.SLIME)) {
                airLimit += 0.34F;
                groundLimit += 0.34F;
            }

            if (isExempt(ExemptType.UNDERBLOCK)) {
                airLimit += 0.91F;
                groundLimit += 0.91F;
            }

            if (groundTicks < 7) {
                groundLimit += (0.25F / groundTicks);
            }

            if(data.getPlayer().getWalkSpeed() > 0.30 && data.getPlayer().isOnGround()) {
                return;
            }

            if (data.getVelocityProcessor().isTakingVelocity()) {
                groundLimit += data.getVelocityProcessor().getVelocityXZ() + 0.05;
                airLimit += data.getVelocityProcessor().getVelocityXZ() + 0.05;
            }

            final boolean exempt = isExempt(ExemptType.NEARSLABS, ExemptType.NEARICE, ExemptType.LAGGINGHARD, ExemptType.LAGGING, ExemptType.DEAD, ExemptType.ICE, ExemptType.BOAT, ExemptType.NEARSTAIRS, ExemptType.RESPAWN, ExemptType.VEHICLE, ExemptType.PISTON, ExemptType.FLYING, ExemptType.TELEPORT, ExemptType.CHUNK);

            if (!exempt) {
                if (airTicks > 0) {
                    if (deltaXZ > airLimit) {
                        if (increaseBuffer() > 3) {
                            fail();
                        }
                    } else {
                        decreaseBufferBy(0.15);
                    }
                } else {
                    if (deltaXZ > groundLimit) {
                        if (increaseBuffer() > 3) {
                            fail();
                        }
                    } else {
                        decreaseBufferBy(0.15);
                    }
                }
            }
        }
    }
}
