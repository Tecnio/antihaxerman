package me.tecnio.antihaxerman.checks.impl.combat.aim;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "Aim", type = "D")
public final class AimD extends Check {
    public AimD(PlayerData data) {
        super(data);
    }

    /*
     * Skidded from https://github.com/GladUrBad/Medusa/
     */

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (isRotationPacket(e)) {
            final float yawAccel = Math.abs(data.getDeltaYaw() - data.getLastDeltaYaw());

            if (data.getDeltaYaw() != 0.0 && data.getLastDeltaYaw() != 0.0 && data.teleportTicks() > 10) {
                if (yawAccel == 0.0) {
                    if (++buffer > 8) {
                        flag(data, "improbable yaw acceleration for long time. yawAccel: " + yawAccel);
                    }
                }else buffer = Math.max(buffer - 1, 0);
            }
        }
    }
}
