package me.tecnio.antihaxerman.checks.impl.movement.noslow;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "NoSlow", type = "B")
public final class NoSlowB extends Check {
    public NoSlowB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        if (data.getPlayer().isBlocking() && data.isBlocking() && data.isSprinting()) {
            flag(data, "sprinting while blocking.");
        }
    }
}
