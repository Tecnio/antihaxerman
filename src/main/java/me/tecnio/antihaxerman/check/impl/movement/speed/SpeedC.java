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

@CheckInfo(name = "Speed", type = "C", description = "Checks if player is going faster than possible", experimental = true)
public final class SpeedC extends Check {
    public SpeedC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final int groundTicks = data.getPositionProcessor().getGroundTicks();
            final int iceTicks = data.getPositionProcessor().getSinceIceTicks();
            final int slimeTicks = data.getPositionProcessor().getSinceSlimeTicks();
            final int blockNearHeadTicks = data.getPositionProcessor().getSinceBlockNearHeadTicks();

            final boolean nearStair = data.getPositionProcessor().isNearStair();

            final boolean takingVelocity = data.getVelocityProcessor().isTakingVelocity();

            final double velocityX = data.getVelocityProcessor().getVelocityX();
            final double velocityZ = data.getVelocityProcessor().getVelocityZ();
            final double velocityXZ = Math.hypot(velocityX, velocityZ);

            double limit = groundTicks > 8 ? PlayerUtil.getBaseGroundSpeed(data.getPlayer()) : PlayerUtil.getBaseSpeed(data.getPlayer());

            if (iceTicks < 40 || slimeTicks < 40) limit += 0.34;
            if (blockNearHeadTicks < 40) limit += 0.91;
            if (nearStair) limit += 0.34;
            if (takingVelocity) limit += velocityXZ + 0.5;

            final boolean exempt = isExempt(ExemptType.VEHICLE, ExemptType.PISTON, ExemptType.FLYING, ExemptType.TELEPORT, ExemptType.CHUNK);
            final boolean invalid = deltaXZ > limit;

            if (invalid && !exempt) {
                if (increaseBuffer() > 5) {
                    fail();
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
