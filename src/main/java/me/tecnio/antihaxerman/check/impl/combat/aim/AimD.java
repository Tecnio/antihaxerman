

package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Aim", type = "D", description = "Checks for unlikely pitch deltas.")
public final class AimD extends Check {
    public AimD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && !isExempt(ExemptType.JOINED)) {
            final float pitch = data.getRotationProcessor().getPitch();
            if (pitch > 89 || pitch < 1) return;

            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean invalid = deltaPitch == 0.0F && deltaYaw >= 15.6F;

            if (invalid) {
                if (increaseBuffer() > 7) {
                    fail("DeltaPitch: " + deltaPitch + " DeltaYaw:" + deltaYaw);
                }
            } else {
                decreaseBufferBy(0.5);
            }
        }
    }
}
