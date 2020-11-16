package me.tecnio.antihaxerman.check.impl.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "BadPackets", type = "A")
public final class BadPacketsA extends Check {
    public BadPacketsA(PlayerData data) {
        super(data);
    }

    @Override
    public void onFlying() {
        if (Math.abs(data.getLocation().getPitch()) > (data.climbableTicks() < 5 ? 91.11 : 90.0)) {
            flag();
        }
    }
}
