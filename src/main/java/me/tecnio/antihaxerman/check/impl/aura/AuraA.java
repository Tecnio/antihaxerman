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
