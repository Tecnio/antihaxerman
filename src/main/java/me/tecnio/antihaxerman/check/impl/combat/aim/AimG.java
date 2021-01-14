/*
 *  Copyright (C) 2020 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
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

package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

import java.util.ArrayDeque;
import java.util.Deque;

@CheckInfo(name = "Aim", type = "G", description = "Checks for dumb rotations.")
public final class AimG extends Check {

    private final Deque<Float> yawSamples = new ArrayDeque<>();
    private final Deque<Float> pitchSamples = new ArrayDeque<>();

    private double lastPitchDeviation;
    private double lastYawDeviation;

    public AimG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final float yawAccel = data.getRotationProcessor().getYawAccel();
            final float pitchAccel = data.getRotationProcessor().getPitchAccel();

            if (deltaPitch > 0.5F && deltaYaw > 0.5F) {
                yawSamples.add(yawAccel);
                pitchSamples.add(pitchAccel);

                if (yawSamples.size() >= 40 && pitchSamples.size() > 40) {
                    final double yawDeviation = MathUtil.getStandardDeviation(yawSamples);
                    final double pitchDeviation = MathUtil.getStandardDeviation(pitchSamples);

                    final double yawDifference = Math.abs(yawDeviation - lastYawDeviation);
                    final double pitchDifference = Math.abs(pitchDeviation - lastPitchDeviation);

                    //debug(String.format("yawDiff: %.3f pitchDiff: %.3f", yawDifference, pitchDifference));

                    lastYawDeviation = yawDeviation;
                    lastPitchDeviation = pitchDeviation;

                    yawSamples.clear();
                    pitchSamples.clear();
                }
            }
        }
    }
}
