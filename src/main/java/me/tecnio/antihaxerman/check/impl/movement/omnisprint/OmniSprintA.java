

package me.tecnio.antihaxerman.check.impl.movement.omnisprint;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@CheckInfo(name = "OmniSprint", type = "A", description = "Detects sprinting in a wrong direction.")
public final class OmniSprintA extends Check {
    public OmniSprintA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isOnGround();
            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double yaw = data.getRotationProcessor().getYaw();
            final Vector direction = new Vector(-Math.sin(yaw * Math.PI / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(yaw * Math.PI / 180.0F) * (float) 1 * 0.5F);

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final Vector move = new Vector(deltaX, 0.0, deltaZ);
            final double delta = move.distanceSquared(direction);

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.WEB, ExemptType.VELOCITY, ExemptType.CHUNK, ExemptType.UNDERBLOCK, ExemptType.ICE);
            final boolean invalid = delta > getLimit() && deltaXZ > 0.1 && sprinting && onGround;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                resetBuffer();
            }
        }
    }

    private double getLimit() {
        return data.getPlayer().getWalkSpeed() > 0.2f ? .23 * 1 + ((data.getPlayer().getWalkSpeed() / 0.2f) * 0.36) : 0.23 + (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.062f);
    }
}
