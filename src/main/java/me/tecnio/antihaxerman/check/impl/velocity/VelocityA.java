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

@CheckInfo(name = "Velocity", type = "A")
public final class VelocityA extends Check {
    public VelocityA(PlayerData data) {
        super(data);
    }

    @Override
    public void onFlying() {
        if (data.getVelocityTicks() == 1) {
            final double velTaken = data.getDeltaY();
            final double velExpected = data.getLastVelocity().getY() * 0.999F;

            final double percentage = (velTaken * 100) / velExpected;

            final boolean exempt = data.liquidTicks() < 20 || data.pistonTicks() < 10 || data.climbableTicks() < 20 || data.collidedVTicks() < 20 || data.teleportTicks() < 20 || data.flyingTicks() < 20;

            if (velTaken < velExpected && !exempt) {
                if (increaseBuffer() > 3) {
                    flag("(Vertical) percentage: " + percentage);
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
