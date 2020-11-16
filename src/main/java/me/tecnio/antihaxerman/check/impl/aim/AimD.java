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
