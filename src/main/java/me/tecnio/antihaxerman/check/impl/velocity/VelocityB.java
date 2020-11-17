/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

package me.tecnio.antihaxerman.check.impl.velocity;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Velocity", type = "B")
public final class VelocityB extends Check {
    public VelocityB(PlayerData data) {
        super(data);
    }

    @Override
    public void onFlying() {
        if (data.getVelocityTicks() == 1) {
            final double expectedVelX = data.getLastVelocity().getX() * (data.attackTicks() < 2 ? 0.6 : 1.0);
            final double expectedVelZ = data.getLastVelocity().getZ() * (data.attackTicks() < 2 ? 0.6 : 1.0);
            final double expectedHorizontalVel = Math.hypot(expectedVelX, expectedVelZ);

            final double velTaken = data.getDeltaXZ();

            final double percentage = (velTaken / expectedHorizontalVel) * 100;

            final boolean exempt = data.liquidTicks() < 20 || data.pistonTicks() < 10 || data.teleportTicks() < 20 || data.isInWeb() || data.collidedHTicks() < 20;

            if (exempt) return;

            if (percentage < 30 || percentage > 300) {
                if (increaseBuffer() > 3) {
                    flag();
                }
            } else {
                decreaseBufferBy(1.5);
            }
        }
    }
}
