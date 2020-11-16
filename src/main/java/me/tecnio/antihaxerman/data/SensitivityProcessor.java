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
