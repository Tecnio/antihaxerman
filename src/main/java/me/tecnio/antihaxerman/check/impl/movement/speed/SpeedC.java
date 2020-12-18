/*
 *  Copyright (C) 2020 Tecnio
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
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;

@CheckInfo(name = "Speed", type = "C", description = "Checks if player is going faster than possible")
public final class SpeedC extends Check {
    public SpeedC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final boolean onGround = data.getPositionProcessor().isOnGround();

            final int groundTicks = data.getPositionProcessor().getGroundTicks();
            final int airTicks = data.getPositionProcessor().getAirTicks();

            final int iceTicks = data.getPositionProcessor().getSinceIceTicks();
            final int slimeTicks = data.getPositionProcessor().getSinceSlimeTicks();

            final boolean blockNearHead = data.getPositionProcessor().isBlockNearHead();
            final boolean takingVelocity = data.getVelocityProcessor().isTakingVelocity();

            double maxGroundSpeed = PlayerUtil.getBaseGroundSpeed(data.getPlayer());
            double maxAirSpeed = PlayerUtil.getBaseSpeed(data.getPlayer());

            if (takingVelocity) {
                final double velocityX = data.getVelocityProcessor().getVelocityX();
                final double velocityZ = data.getVelocityProcessor().getVelocityZ();

                final double velocityXZ = Math.hypot(velocityX, velocityZ);

                maxAirSpeed += velocityXZ + 0.5;
                maxGroundSpeed += velocityXZ + 0.5;
            }

            if (groundTicks <= 5) maxGroundSpeed += 0.15;
            if (airTicks == 0) maxAirSpeed += 0.3;

            if (blockNearHead) {
                maxAirSpeed += 0.17;
                maxGroundSpeed += 0.095;
            }

            if (iceTicks < 40) {
                maxAirSpeed += 0.2;
                maxGroundSpeed += 0.15;
            }

            if (slimeTicks < 40) {
                maxAirSpeed += 0.2;
            }

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.FLYING, ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE);

            handle: {
                if (exempt) break handle;

                if (onGround) {
                    if (deltaXZ > maxGroundSpeed) {
                        if (increaseBuffer() > 3) {
                            fail();
                        }
                    } else {
                        decreaseBufferBy(0.25);
                    }
                } else {
                    if (deltaXZ > maxAirSpeed) {
                        if (increaseBuffer() > 3) {
                            fail();
                        }
                    } else {
                        decreaseBufferBy(0.25);
                    }
                }
            }
        }
    }
}
