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

package me.tecnio.antihaxerman.check.impl.largemove;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "LargeMove", type = "A", maxVL = 5)
public final class LargeMoveA extends Check {
    public LargeMoveA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final boolean exempt = data.isTakingVelocity() || data.pistonTicks() < 10 || data.teleportTicks() < 20 || data.getTick() < 20;

        if (data.getDeltaXZ() > 10.0 && !exempt) {
            flag();
        }
    }
}
