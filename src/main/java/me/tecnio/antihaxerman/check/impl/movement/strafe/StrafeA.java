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

@CheckInfo(name = "Strafe", type = "A", description = "Checks for invalid strafing.", experimental = true)
public final class StrafeA extends Check {

    public StrafeA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            final int airTicks = data.getPositionProcessor().getClientAirTicks();

            final double blockSlipperiness = 0.91F;
            final double attributeSpeed = 0.026;

            final double predictedDeltaX = lastDeltaX * blockSlipperiness;
            final double predictedDeltaZ = lastDeltaZ * blockSlipperiness;

            final double diffX = Math.abs(deltaX - predictedDeltaX);
            final double diffZ = Math.abs(deltaZ - predictedDeltaZ);

            final boolean exempt = isExempt(ExemptType.TELEPORT_DELAY_SMALL, ExemptType.PISTON, ExemptType.FLYING,
                    ExemptType.UNDERBLOCK, ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.VELOCITY,
                    ExemptType.CHUNK, ExemptType.NEAR_WALL, ExemptType.GHOST_BLOCK);
            final boolean invalid = (diffX > attributeSpeed || diffZ > attributeSpeed) && deltaXZ > .05 && airTicks > 2;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.1);
            }
        }
    }
}
