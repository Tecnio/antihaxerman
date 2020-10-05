package me.tecnio.antihaxerman.checks.impl.player.badpackets;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "BadPackets", type = "F")
public class BadPacketsF extends Check {
    public BadPacketsF(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        if (PlayerUtils.isOnSlime(data))return;

        if (data.getAirTicks() == 1 && data.getGroundTicks() > 0 && data.getDeltaY() == 0) {
            if (++buffer > 3) {
                flag(data, "");
            }
        }else buffer = Math.max(0, buffer - 0.25);
    }
}