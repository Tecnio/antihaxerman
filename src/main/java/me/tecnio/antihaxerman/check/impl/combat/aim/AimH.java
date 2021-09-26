

package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Aim", type = "H", description = "Checks for unlikely yaw deltas.")
public final class AimH extends Check {

    public AimH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean invalid = deltaYaw == 0.0F && deltaPitch >= 20.0F;

            if (invalid) {
                if (increaseBuffer() > 1) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }
}
