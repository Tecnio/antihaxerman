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

package me.tecnio.antihaxerman.check.impl.flight;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Flight", type = "C")
public final class FlightC extends Check {
    public FlightC(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final double diff = Math.abs(data.getDeltaY() - data.getLastDeltaY());

        final boolean exempt = data.getServerAirTicks() < 3 || data.pistonTicks() < 10 || data.flyingTicks() < 40 || data.isNearBoat() || data.liquidTicks() < 20 || data.climbableTicks() < 20 || data.teleportTicks() < 40 || data.getPlayer().isInsideVehicle() || data.isTakingVelocity() || data.isInWeb() || data.isInVoid();

        if (diff < 0.01 && !exempt) {
            if (increaseBuffer() > 4) {
                flag();
            }
        } else {
            decreaseBufferBy(0.5);
        }
    }
}
