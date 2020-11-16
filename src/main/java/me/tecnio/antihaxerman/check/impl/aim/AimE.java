package me.tecnio.antihaxerman.check.impl.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Aim", type = "E")
public final class AimE extends Check {
    public AimE(PlayerData data) {
        super(data);
    }

    @Override
    public void onRotation() {
        if (data.attackTicks() < 1) {
            if (data.getDeltaYaw() % .25 == 0.0 && data.getDeltaYaw() > 0) {
                if (increaseBuffer() > 3) {
                    flag();
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
