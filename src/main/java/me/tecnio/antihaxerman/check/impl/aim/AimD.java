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

package me.tecnio.antihaxerman.check.impl.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.math.MathUtils;

@CheckInfo(name = "Aim", type = "D")
public final class AimD extends Check {
    public AimD(PlayerData data) {
        super(data);
    }

    private int bufferYaw, bufferPitch;

    @Override
    public void onRotation() {
        if (MathUtils.isScientificNotation(data.getDeltaYaw()) && data.getDeltaYaw() > 0.1) {
            bufferYaw = Math.min(bufferYaw + 1, Integer.MAX_VALUE);
            if (bufferYaw > 5) {
                flag();
            }
        } else {
            bufferYaw = Math.max(bufferYaw - 1, 0);
        }

        if (MathUtils.isScientificNotation(data.getDeltaPitch()) && data.getDeltaPitch() > 0.1) {
            bufferPitch = Math.min(bufferPitch + 1, Integer.MAX_VALUE);
            if (bufferPitch > 5) {
                flag();
            }
        } else {
            bufferPitch = Math.max(bufferPitch - 1, 0);
        }
    }
}
