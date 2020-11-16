package me.tecnio.antihaxerman.check.impl.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Aim", type = "B")
public final class AimB extends Check {
    public AimB(PlayerData data) {
        super(data);
    }

    @Override
    public void onRotation() {
        if (data.getSensitivityAsPercentage() < 0.0) {
            if (increaseBuffer() > 5) {
                flag();
            }
        } else {
            decreaseBufferBy(0.5);
        }
    }
}
