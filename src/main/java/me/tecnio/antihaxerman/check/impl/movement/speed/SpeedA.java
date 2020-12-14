package me.tecnio.antihaxerman.check.impl.movement.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.BlockUtil;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "A", description = "Detects speed cheats based on prediction.")
public final class SpeedA extends Check {

    private double blockSlipperiness = 0.91;
    private double lastHorizontalDistance = 0.0;

    public SpeedA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final Player player = data.getPlayer();

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            double blockSlipperiness = this.blockSlipperiness;
            double attributeSpeed = 1.d;

            final boolean onGround = data.getPositionProcessor().isOnGround();
            final boolean sprinting = data.getActionProcessor().isSprinting();
            final boolean exempt = this.isExempt(
                    ExemptType.TPS,
                    ExemptType.TELEPORT,
                    ExemptType.PISTON,
                    ExemptType.FLYING,
                    ExemptType.UNDERBLOCK,
                    ExemptType.VEHICLE);

            final int modifierJump = PlayerUtil.getPotionLevel(player, PotionEffectType.JUMP);
            
            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SPEED) * (float) 0.2 * attributeSpeed;

            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SLOW) * (float) -.15 * attributeSpeed;

            if (onGround) {
                blockSlipperiness *= 0.91f;

                if (sprinting) attributeSpeed *= 1.3;
                attributeSpeed *= 0.16277136 / Math.pow(blockSlipperiness, 3);

                if (deltaY > 0.4199 + modifierJump * 0.1 && sprinting) {
                    attributeSpeed += 0.2;
                }
            } else {
                attributeSpeed = sprinting ? 0.026 : 0.02;

                blockSlipperiness = 0.91f;
            }

            final boolean takingVelocity = data.getVelocityProcessor().isTakingVelocity();

            final double velocityX = data.getVelocityProcessor().getVelocityX();
            final double velocityZ = data.getVelocityProcessor().getVelocityZ();

            final double velocityXZ = Math.hypot(velocityX, velocityZ);

            if (takingVelocity) attributeSpeed += velocityXZ;

            final double horizontalDistance = Math.hypot(deltaX, deltaZ);
            final double movementSpeed = (horizontalDistance - lastHorizontalDistance) / attributeSpeed;


            if (movementSpeed > 1.0 && !exempt) {
                increaseBufferBy(10);

                if (getBuffer() > 40) {
                    fail();

                    multiplyBuffer(0.5);
                }
            } else {
                decreaseBuffer();
            }

            final double x = data.getPositionProcessor().getX();
            final double y = data.getPositionProcessor().getY();
            final double z = data.getPositionProcessor().getZ();

            final Location blockLocation = new Location(data.getPlayer().getWorld(), x, (Math.floor(y) - 0.01), z);

            this.blockSlipperiness = BlockUtil.getBlockFriction(blockLocation) * 0.91F;
            this.lastHorizontalDistance = horizontalDistance * blockSlipperiness;
        }
    }
}
