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

package me.tecnio.antihaxerman.check.impl.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Speed", type = "B")
public final class SpeedB extends Check {
    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final double prediction = data.getLastDeltaXZ() * 0.91F + 0.02 + (data.isSprinting() ? 0.0063 : 0.0);
        final double diff = data.getDeltaXZ() - prediction;

        final boolean exempt = data.getDeltaXZ() < 0.1 || data.pistonTicks() < 10 || data.getAirTicks() < 3 || data.getPlayer().isFlying() || data.liquidTicks() < 10 || data.isTakingVelocity() || data.teleportTicks() < 10 || data.collidedVTicks() < 10 || data.getPlayer().isInsideVehicle();

        if (diff > 1E-12 && prediction > 0.075 && !exempt) {
            if (increaseBuffer() > 5) {
                flag();
            }
        } else {
            decreaseBufferBy(2.5);
        }
    }
}
