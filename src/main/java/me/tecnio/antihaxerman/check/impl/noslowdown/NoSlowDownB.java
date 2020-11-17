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

package me.tecnio.antihaxerman.check.impl.noslowdown;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "NoSlowDown", type = "B")
public final class NoSlowDownB extends Check {
    public NoSlowDownB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final boolean exempt = data.getGroundTicks() > 10;

        final boolean invalid = data.isSneaking() && data.isSprinting();

        if (invalid && !exempt) {
            if (increaseBuffer() > 10) {
                flag();
            }
        } else {
            decreaseBufferBy(2.5);
        }
    }
}
