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
            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            final int airTicks = data.getPositionProcessor().getClientAirTicks();

            double blockSlipperiness = 0.91F;
            double attributeSpeed = sprinting ? 0.026 : 0.02;;

            final double predictedDeltaX = lastDeltaX * blockSlipperiness + attributeSpeed ;
            final double predictedDeltaZ = lastDeltaZ * blockSlipperiness + attributeSpeed ;

            final double diffX = deltaX - predictedDeltaX;
            final double diffZ = deltaZ - predictedDeltaZ;

            final boolean exempt = this.isExempt(ExemptType.TPS, ExemptType.TELEPORT, ExemptType.PISTON, ExemptType.FLYING, ExemptType.UNDERBLOCK, ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.VELOCITY, ExemptType.UNDERBLOCK, ExemptType.CHUNK);
            final boolean invalid = (diffX > 0.01 || diffZ > 0.01) && deltaXZ > .175 && airTicks > 2;

            if (invalid && !exempt) {
                if (increaseBuffer() > 3) {
                    fail(String.format("diffX: %.3f diffZ: %.3f airT: %s", diffX, diffZ, airTicks));
                }
            } else {
                decreaseBufferBy(0.1);
            }

            debug(String.format("diffX: %.2f diffZ: %.2f buffer: %.2f airT: %s", diffX, diffZ, getBuffer(), airTicks));
        }
    }
}
