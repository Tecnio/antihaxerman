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

package me.tecnio.antihaxerman.check.impl.aura;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.math.MathUtils;

@CheckInfo(name = "Aura", type = "A")
public final class AuraA extends Check {
    public AuraA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final double accel = Math.abs(data.getDeltaXZ() - data.getLastDeltaXZ());

        final boolean exempt = data.getDeltaXZ() < 0.21 || data.attackTicks() > 1 || !data.isSprinting();

        if (MathUtils.isScientificNotation(accel) && !exempt) {
            if (increaseBuffer() > 2) {
                flag();
            }
        } else {
            decreaseBufferBy(0.01);
        }
    }
}
