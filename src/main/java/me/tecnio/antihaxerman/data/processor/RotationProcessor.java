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

package me.tecnio.antihaxerman.data.processor;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.util.MathUtil;
import lombok.Getter;
import me.tecnio.antihaxerman.data.PlayerData;

import java.util.ArrayDeque;

@Getter
public final class RotationProcessor {

    private final PlayerData data;
    private float yaw, pitch, lastYaw, lastPitch,
    deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch,
    yawAccel, pitchAccel, lastYawAccel, lastPitchAccel, gcd, actualGcd;

    private int mouseDeltaX, mouseDeltaY, lastMouseDeltaX, lastMouseDeltaY;

    private double finalSensitivity, cinematicTicks;

    private final ArrayDeque<Integer> sensitivitySamples = new ArrayDeque<>();

    private int sensitivity, lastCinematic;

    private boolean cinematic;

    public RotationProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final float yaw, final float pitch) {
        lastYaw = this.yaw;
        lastPitch = this.pitch;

        this.yaw = yaw;
        this.pitch = pitch;

        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;

        deltaYaw = Math.abs(yaw - lastYaw);
        deltaPitch = Math.abs(pitch - lastPitch);

        lastPitchAccel = pitchAccel;
        lastYawAccel = yawAccel;

        yawAccel = Math.abs(deltaYaw - lastDeltaYaw);
        pitchAccel = Math.abs(deltaPitch - lastDeltaPitch);

        processCinematic();

        if (deltaPitch > 0 && deltaPitch < 30) {
            processSensitivity();
        }

        if (gcd != 0) {
            lastMouseDeltaX = mouseDeltaX;
            lastMouseDeltaY = mouseDeltaY;

            this.mouseDeltaX = (int)(deltaYaw / gcd);
            this.mouseDeltaY = (int)(deltaPitch / gcd);
        }
    }

    private void processCinematic() {
        final float yawAccelAccel = Math.abs(yawAccel - lastYawAccel);
        final float pitchAccelAccel = Math.abs(pitchAccel - lastPitchAccel);

        final boolean invalidYaw = yawAccelAccel < .05 && yawAccelAccel > 0;
        final boolean invalidPitch = pitchAccelAccel < .05 && pitchAccelAccel > 0;

        final boolean exponentialYaw = MathUtil.isExponentiallySmall(yawAccelAccel);
        final boolean exponentialPitch = MathUtil.isExponentiallySmall(pitchAccelAccel);

        if (finalSensitivity < 100 && (exponentialYaw || exponentialPitch)) {
            cinematicTicks += 3.5;
        } else if (invalidYaw || invalidPitch) {
            cinematicTicks += 1.75;
        } else {
            if (cinematicTicks > 0) cinematicTicks -= .6;
        }
        if (cinematicTicks > 20) {
            cinematicTicks -= 1.5;
        }

        cinematic = cinematicTicks > 7.5 || (AntiHaxerman.INSTANCE.getTickManager().getTicks() - lastCinematic < 120);

        if (cinematic && cinematicTicks > 7.5) {
            lastCinematic = AntiHaxerman.INSTANCE.getTickManager().getTicks();
        }
    }

    private void processSensitivity() {
        final float gcd = (float) MathUtil.getGcd(deltaPitch, lastDeltaPitch);
        final double sensitivityModifier = Math.cbrt(0.8333 * gcd);
        final double sensitivityStepTwo = (1.666 * sensitivityModifier) - 0.3333;
        final double finalSensitivity = sensitivityStepTwo * 200;

        this.finalSensitivity = finalSensitivity;

        sensitivitySamples.add((int)finalSensitivity);

        if (sensitivitySamples.size() >= 40) {
            this.sensitivity = MathUtil.getMode(sensitivitySamples);

            final float gcdOne = (sensitivity / 200F) * 0.6F + 0.2F;

            this.gcd = gcdOne * gcdOne * gcdOne * 1.2F;
            this.actualGcd = gcdOne * gcdOne * gcdOne * 8.0F;

            sensitivitySamples.clear();
        }
    }
}