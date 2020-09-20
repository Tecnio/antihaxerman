package me.tecnio.antihaxerman.checks.impl.combat.aim;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.MathUtils;

@CheckInfo(name = "Aim", type = "B")
public final class AimB extends Check {
    public AimB(PlayerData data) {
        super(data);
    }

    /*
    * Skidded from https://github.com/GladUrBad/Medusa/
     */

    private int yawBuffer, pitchBuffer;

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (isRotationPacket(e)) {
            if (MathUtils.isScientificNotation(data.getDeltaYaw()) && data.getDeltaYaw() > 0.5) {
                if (++yawBuffer > 4) {
                    flag(data, "invalid yaw rotations. sn: true, dYaw: " + data.getDeltaYaw());
                }
            }else yawBuffer = Math.max(preVL - 2, 0);

            if (MathUtils.isScientificNotation(data.getDeltaPitch()) && data.getDeltaPitch() > 0.5) {
                if (++pitchBuffer > 4) {
                    flag(data, "invalid yaw rotations. sn: true, dYaw: " + data.getDeltaYaw());
                }
            }else pitchBuffer = Math.max(preVL - 2, 0);
        }
    }
}
