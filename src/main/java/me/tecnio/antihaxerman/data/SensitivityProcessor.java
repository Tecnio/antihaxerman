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

package me.tecnio.antihaxerman.data;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.utils.math.MathUtils;

import java.util.ArrayDeque;

@CheckInfo(name = "Sensitivity", type = "Processor", autoBan = false)
public final class SensitivityProcessor extends Check {
    public SensitivityProcessor(PlayerData data) {
        super(data);
    }

    private final ArrayDeque<Integer> sensitivitySamples = new ArrayDeque<>();

    @Override
    public void onRotation() {
        final float deltaPitch = data.getDeltaPitch();
        final float lastDeltaPitch = data.getLastDeltaPitch();

        if (deltaPitch > 0 && deltaPitch < 30) {
            final float gcd = (float) MathUtils.getGcd(deltaPitch, lastDeltaPitch);
            final double sensitivityModifier = Math.cbrt(0.8333 * gcd);
            final double sensitivityStepTwo = (1.666 * sensitivityModifier) - 0.3333;
            final double sensitivity = sensitivityStepTwo * 200;

            data.setSensitivity(sensitivity);

            sensitivitySamples.add((int) sensitivity);

            if (sensitivitySamples.size() >= 40) {
                data.setSensitivityAsPercentage(MathUtils.getMode(sensitivitySamples));

                final float gcdOne = (data.getSensitivityAsPercentage() / 200F) * 0.6F + 0.2F;
                data.setGcd(gcdOne * gcdOne * gcdOne * 1.2F);

                sensitivitySamples.clear();
            }
        }
    }
}
