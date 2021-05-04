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

            // Straight from MCP so if you think its dumb fuck off.
            if (Math.abs(deltaY - jumpMotion) < 1.0E-4 && airTicks == 1 && sprinting) {
                final float f = data.getRotationProcessor().getYaw() * 0.017453292F;

                final double x = lastDeltaX - (Math.sin(f) * 0.2F);
                final double z = lastDeltaZ + (Math.cos(f) * 0.2F);

                airLimit += Math.hypot(x, z);
            }

            if (data.getPositionProcessor().isNearStair()) {
                airLimit += 0.91F;
                groundLimit += 0.91F;
            }

            if (isExempt(ExemptType.ICE, ExemptType.SLIME)) {
                airLimit += 0.34F;
                groundLimit += 0.34F;
            }

            if (data.getPositionProcessor().getSinceBlockNearHeadTicks() < 3) {
                airLimit += 0.91F;
                groundLimit += 0.91F;
            }

            if (groundTicks < 7) {
                groundLimit += (0.25F / groundTicks);
            }

            if (data.getVelocityProcessor().isTakingVelocity()) {
                groundLimit += data.getVelocityProcessor().getVelocityXZ() + 0.05;
                airLimit += data.getVelocityProcessor().getVelocityXZ() + 0.05;
            }

            // Problematic way of fixing it but good enough.
            if (data.getPositionProcessor().getSinceTeleportTicks() < 15) {
                airLimit += 0.1;
                groundLimit += 0.1;
            }

            final boolean exempt = isExempt(
                    ExemptType.VEHICLE, ExemptType.PISTON,
                    ExemptType.FLYING, ExemptType.TELEPORT, ExemptType.CHUNK,

                    ExemptType.VELOCITY, ExemptType.VELOCITY_ON_TICK, ExemptType.VELOCITY_RECENTLY
            );

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
