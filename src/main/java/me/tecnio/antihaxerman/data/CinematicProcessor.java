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

import com.google.common.collect.Lists;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.utils.math.GraphUtils;

import java.util.List;

@CheckInfo(name = "Cinematic", type = "Processor", autoBan = false)
public final class CinematicProcessor extends Check {
    public CinematicProcessor(PlayerData data) {
        super(data);
    }

    private long lastSmooth = 0L, lastHighRate = 0L;

    private final List<Double> yawSamples = Lists.newArrayList();
    private final List<Double> pitchSamples = Lists.newArrayList();

    @Override
    public void onRotation() {
        final long now = System.currentTimeMillis();

        final double deltaYaw = data.getDeltaYaw();
        final double deltaPitch = data.getDeltaPitch();

        final double lastDeltaYaw = data.getLastDeltaYaw();
        final double lastDeltaPitch = data.getLastDeltaPitch();

        final double differenceYaw = Math.abs(deltaYaw - lastDeltaYaw);
        final double differencePitch = Math.abs(deltaPitch - lastDeltaPitch);

        final double joltYaw = Math.abs(differenceYaw - deltaYaw);
        final double joltPitch = Math.abs(differencePitch - deltaPitch);

        final boolean cinematic = (now - lastHighRate > 250L) || now - lastSmooth < 9000L;

        if (joltYaw > 1.0 && joltPitch > 1.0) {
            this.lastHighRate = now;
        }

        if (deltaPitch > 0.0 && deltaPitch > 0.0) {
            yawSamples.add(deltaYaw);
            pitchSamples.add(deltaPitch);
        }

        if (yawSamples.size() == 20 && pitchSamples.size() == 20) {
            // Get the cerberus/positive graph of the sample-lists
            final GraphUtils.GraphResult resultsYaw = GraphUtils.getGraph(yawSamples);
            final GraphUtils.GraphResult resultsPitch = GraphUtils.getGraph(pitchSamples);

            // Negative values
            final int negativesYaw = resultsYaw.getNegatives();
            final int negativesPitch = resultsPitch.getNegatives();

            // Positive values
            final int positivesYaw = resultsYaw.getPositives();
            final int positivesPitch = resultsPitch.getPositives();

            // Cinematic camera usually does this on *most* speeds and is accurate for the most part.
            if (positivesYaw > negativesYaw || positivesPitch > negativesPitch) {
                this.lastSmooth = now;
            }

            yawSamples.clear();
            pitchSamples.clear();
        }

        data.setUsingCinematic(cinematic);
    }
}
