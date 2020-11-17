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

@CheckInfo(name = "Flight", type = "A")
public final class FlightA extends Check {
    public FlightA(PlayerData data) {
        super(data);
    }

    @Override
    public void onFlying() {
        final double predicted = (data.getLastDeltaY() - 0.08) * 0.9800000190734863;
        final double diff = Math.abs(data.getDeltaY() - predicted);

        final boolean exempt = data.getAirTicks() < 7 || data.isOnServerGround() || data.isTakingVelocity() || data.pistonTicks() < 10 || data.liquidTicks() < 10 || data.climbableTicks() < 10 || data.isNearBoat() || data.getPlayer().getVelocity().getY() >= -0.075D || data.flyingTicks() < 20 || data.getPlayer().isInsideVehicle() || data.isInWeb();

        if (diff > 0.001 && Math.abs(predicted) >= 0.005 && !exempt) {
            if (increaseBuffer() > 4) {
                flag();
            }
        } else {
            decreaseBufferBy(0.25);
        }
    }
}
