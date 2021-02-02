/*
 *  Copyright (C) 2020 - 2021 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.check.impl.movement.strafe;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.BlockUtil;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Strafe", type = "A", description = "Checks for invalid strafing.", experimental = true)
public final class StrafeA extends Check {

    private double blockSlipperiness = 0.91;

    public StrafeA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final Player player = data.getPlayer();

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            double blockSlipperiness = this.blockSlipperiness;
            double attributeSpeed = 1.d;

            final boolean onGround = data.getPositionProcessor().isOnGround();
            final boolean sprinting = data.getActionProcessor().isSprinting();

            final int airTicks = data.getPositionProcessor().getClientAirTicks();
            final int groundTicks = data.getPositionProcessor().getGroundTicks();

            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SPEED) * (float) 0.2 * attributeSpeed;
            attributeSpeed += PlayerUtil.getPotionLevel(player, PotionEffectType.SLOW) * (float) -.15 * attributeSpeed;

            if (onGround) {
                blockSlipperiness *= 0.91f;

                if (sprinting) attributeSpeed *= 1.3;
                attributeSpeed *= 0.16277136 / Math.pow(blockSlipperiness, 3);

                // getAiSpeed but I can't run it the way I am using right now
                attributeSpeed *= data.getPlayer().getWalkSpeed() / 2.0;
                //cancel not usable in this way
                attributeSpeed = 1000;
            } else {
                attributeSpeed = sprinting ? 0.0263 : 0.02;

                blockSlipperiness = 0.91f;
            }

            final double predictedDeltaX = lastDeltaX * blockSlipperiness + attributeSpeed;
            final double predictedDeltaZ = lastDeltaZ * blockSlipperiness + attributeSpeed;

            final double diffX = deltaX - predictedDeltaX;
            final double diffZ = deltaZ - predictedDeltaZ;

            final boolean exempt = this.isExempt(ExemptType.TPS, ExemptType.TELEPORT, ExemptType.PISTON, ExemptType.FLYING, ExemptType.UNDERBLOCK, ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.VELOCITY, ExemptType.UNDERBLOCK, ExemptType.CHUNK);
            final boolean invalid = (diffX > 0.01 || diffZ > 0.01) && deltaXZ > .175 && (airTicks > 2 || groundTicks > 2);

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail(String.format("diffX: %.3f diffZ: %.3f airT: %s groundT: %s", diffX, diffZ, airTicks, groundTicks));
                }
            } else {
                decreaseBufferBy(0.1);
            }

            final double x = data.getPositionProcessor().getX();
            final double y = data.getPositionProcessor().getY();
            final double z = data.getPositionProcessor().getZ();

            final Location blockLocation = new Location(data.getPlayer().getWorld(), x, Math.floor(y - 0.1), z);

            this.blockSlipperiness = BlockUtil.getBlockFriction(blockLocation) * 0.91F;

            debug(String.format("diffX: %.2f diffZ: %.2f buffer: %.2f airT: %s", diffX, diffZ, getBuffer(), airTicks));
        }
    }
}
