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

package me.tecnio.antihaxerman.check.impl.movement.fastclimb;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "FastClimb", type = "A", description = "Checks if player is going faster than possible on a climbable.")
public final class FastClimbA extends Check {
    public FastClimbA(final PlayerData data) {
        super(data);
    }

    // Not the best thing. Works tho patches bad fast climbs.

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double acceleration = deltaY - lastDeltaY;

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.PISTON, ExemptType.FLYING, ExemptType.BOAT, ExemptType.VEHICLE);
            final boolean invalid = ((float) deltaY) > 0.1176F && acceleration == 0.0 && data.getPositionProcessor().isOnClimbable();

            if (invalid && !exempt) {
                fail();
            }
        }
    }
}
