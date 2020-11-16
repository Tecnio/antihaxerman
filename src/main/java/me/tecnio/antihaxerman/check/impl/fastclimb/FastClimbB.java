package me.tecnio.antihaxerman.check.impl.fastclimb;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "FastClimb", type = "B")
public final class FastClimbB extends Check {
    public FastClimbB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {

    }
}
