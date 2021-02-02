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

package me.tecnio.antihaxerman.check.impl.combat.velocity;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Velocity", type = "B", description = "Checks for horizontal velocity modifications.")
public final class VelocityB extends Check {
    public VelocityB(final PlayerData data) {
        super(data);
    }

    // TODO: 1/4/2021 Check if near wall

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int ticksSinceVelocity = data.getVelocityProcessor().getTakingVelocityTicks();
            if (ticksSinceVelocity != 1) return;

            final double velocityX = data.getVelocityProcessor().getVelocityX() * (hitTicks() < 2 ? 0.6 : 1.0);
            final double velocityZ = data.getVelocityProcessor().getVelocityZ() * (hitTicks() < 2 ? 0.6 : 1.0);

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double velocityXZ = Math.hypot(velocityX, velocityZ);

            final double percentage = (deltaXZ / velocityXZ) * 100.0;

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE, ExemptType.UNDERBLOCK, ExemptType.TELEPORT, ExemptType.FLYING);
            final boolean invalid = percentage < 10.0 && velocityXZ > 1E-2;

            if (invalid && !exempt) {
                if (increaseBuffer() > 5) {
                    //fail();
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
