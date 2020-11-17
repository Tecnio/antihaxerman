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

package me.tecnio.antihaxerman.check.impl.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Motion", type = "A")
public final class MotionA extends Check {
    public MotionA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final boolean exempt = data.collidedVTicks() < 10 || data.pistonTicks() < 10 || data.slimeTicks() < 10 || data.teleportTicks() < 10;
        final boolean invalid = data.getDeltaY() == -data.getLastDeltaY() && data.getDeltaY() != 0.0;

        if (invalid && !exempt) {
            if (increaseBuffer() > 4) {
                flag();
            }
        } else {
            resetBuffer();
        }
    }
}
