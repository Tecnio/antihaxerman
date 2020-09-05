package me.tecnio.antihaxerman.checks.impl.player.badpackets;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "BadPackets", type = "A")
public class BadPacketsA extends Check {
    @Override
    public void onMove(PlayerData data) {
        if (Math.abs(data.getLocation().getPitch()) > (data.isOnClimbableBlock() ? 91.11 : 90.0)) {
            flag(data, "invalid pitch, p: " + Math.abs(data.getLocation().getPitch()));
        }
    }
}
