

package me.tecnio.antihaxerman.data.processor;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.MathUtil;
import lombok.Getter;

import java.util.ArrayDeque;

@Getter
public final class RotationProcessor {

    private final PlayerData data;
    private float yaw, pitch, lastYaw, lastPitch,
    deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch,
    yawAccel, pitchAccel, lastYawAccel, lastPitchAccel, gcd, actualGcd;

    private int mouseDeltaX, mouseDeltaY, lastMouseDeltaX, lastMouseDeltaY;

    private double finalSensitivity, cinematicTicks;

    private float cinematicTime;

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

            this.mouseDeltaX = Math.round(deltaYaw / gcd);
            this.mouseDeltaY = Math.round(deltaPitch / gcd);
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
        if(cinematic) {
            cinematicTime = System.currentTimeMillis();
        }
        if (cinematic && cinematicTicks > 7.5) {
            lastCinematic = AntiHaxerman.INSTANCE.getTickManager().getTicks();
        }
    }

    private void processSensitivity() {
        final float gcd = (float) MathUtil.getGcd(deltaPitch, lastDeltaPitch);

        final double sensitivityModifier = Math.cbrt(0.83333333333 * gcd);
        final double sensitivityStepTwo = (1.666666666666 * sensitivityModifier) - 0.3333333333333;
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
