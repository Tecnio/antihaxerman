package me.tecnio.antihaxerman.check.impl.movement.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "G", description = "Checks if player is not following minecraft rules.")
public class SpeedG extends Check {
    public SpeedG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            if(data.getPlayer().getWalkSpeed() < 0.2) {
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

                final double x = lastDeltaX - (Math.sin(f) * 0.28F);
                final double z = lastDeltaZ + (Math.cos(f) * 0.28F);

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


            if(data.getPlayer().getWalkSpeed() > 0.98) {
                return;
            }
            if (data.getVelocityProcessor().getVelocityH() != 0) {
                groundLimit += data.getVelocityProcessor().getVelocityH() + 0.05;
                airLimit += data.getVelocityProcessor().getVelocityH() + 0.05;
            }

            if (groundTicks < 7) {
                groundLimit += (0.25F / groundTicks);
            }
            final boolean exempt = isExempt(ExemptType.NEARSTAIRS, ExemptType.VEHICLE, ExemptType.PISTON, ExemptType.FLYING, ExemptType.TELEPORT, ExemptType.CHUNK);

            if (!exempt) {
                if (airTicks > 0) {
                    if (deltaXZ > airLimit) {
                        if (increaseBuffer() > 10) {
                            fail("DeltaXZ: " + deltaXZ + " AirLimit: " + airLimit);
                        }
                    } else {
                        decreaseBufferBy(0.15);
                    }
                } else {
                    if (deltaXZ > groundLimit) {
                        if (increaseBuffer() > 10) {
                            fail("DeltaXZ: " + deltaXZ + " GroundLimit: " + groundLimit);
                        }
                    } else {
                        decreaseBufferBy(0.15);
                    }
                }
            }
        }
    }
}
