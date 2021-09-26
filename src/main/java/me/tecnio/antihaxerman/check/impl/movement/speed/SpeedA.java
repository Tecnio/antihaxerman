

package me.tecnio.antihaxerman.check.impl.movement.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import me.tecnio.antihaxerman.util.type.BlockUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "A", description = "Detects speed cheats based on friction.")
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

            final double deltaY = data.getPositionProcessor().getDeltaY();

            double blockSlipperiness = this.blockSlipperiness;
            double attributeSpeed = 1.d;

            final boolean lastOnGround = data.getPositionProcessor().isLastOnGround();

            final boolean exempt = this.isExempt(ExemptType.WEB, ExemptType.WEBRN, ExemptType.TELEPORT_DELAY, ExemptType.PISTON, ExemptType.VELOCITY_ON_TICK,
                    ExemptType.FLYING, ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.CHUNK, ExemptType.GHOST_BLOCK);

            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SPEED) * (float) 0.2 * attributeSpeed;
            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SLOW) * (float) -.15 * attributeSpeed;

            if (lastOnGround) {
                blockSlipperiness *= 0.91f;

                attributeSpeed *= 1.3;
                attributeSpeed *= 0.16277136 / Math.pow(blockSlipperiness, 3);

                if (deltaY > 0.0) {
                    attributeSpeed += 0.2;
                }
            } else {
                attributeSpeed = 0.026f;
                blockSlipperiness = 0.91f;
            }

            final double horizontalDistance = data.getPositionProcessor().getDeltaXZ();
            final double movementSpeed = (horizontalDistance - lastHorizontalDistance) / attributeSpeed;

            if (movementSpeed > 1.0 && !exempt) {
                increaseBufferBy(10);

                if (getBuffer() > 20) {
                    fail();
                }
            } else {
                decreaseBufferBy(1);
            }

            final double x = data.getPositionProcessor().getX();
            final double y = data.getPositionProcessor().getY();
            final double z = data.getPositionProcessor().getZ();

            final Location blockLocation = new Location(data.getPlayer().getWorld(), x, Math.floor(y) - 1, z);

            this.blockSlipperiness = BlockUtil.getBlockFriction(blockLocation);
            this.lastHorizontalDistance = horizontalDistance * blockSlipperiness;
        }
    }
}
