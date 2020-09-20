package me.tecnio.antihaxerman.checks.impl.combat.aim;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "Aim", type = "C")
public final class AimC extends Check {
    public AimC(PlayerData data) {
        super(data);
    }

    /*
     * Skidded from https://github.com/GladUrBad/Medusa/
     */

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (isRotationPacket(e)) {
            if (data.getDeltaYaw() % .25 == 0 && data.getDeltaYaw() > 0) {
                if (++preVL > 2) {
                    flag(data, "invalid yaw.");
                }
            }else preVL = 0;
        }
    }
}
