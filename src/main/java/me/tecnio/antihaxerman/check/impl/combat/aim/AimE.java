// Decompiled with: CFR 0.152
// Class Version: 8
package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name="Aim", type="E", description="Checks for rounded rotations.")
public final class AimE
extends Check {

    public AimE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation() && this.data.getPositionProcessor().getSinceTeleportTicks() > 2) {
            float deltaYaw = this.data.getRotationProcessor().getDeltaYaw();
            float deltaPitch = this.data.getRotationProcessor().getDeltaPitch();
            float lastDeltaYaw = this.data.getRotationProcessor().getLastDeltaYaw();
            float lastDeltaPitch = this.data.getRotationProcessor().getLastDeltaPitch();
            boolean invalid = (Math.abs(lastDeltaYaw) + Math.abs(lastDeltaPitch)) % 1.0f == 0.0f && (Math.abs(deltaYaw) + Math.abs(deltaPitch)) % 1.0f == 0.0f || deltaYaw != 0.0f && deltaYaw % 1.0f == 0.0f && lastDeltaPitch % 1.0f == 0.0f || deltaYaw != 0.0f && deltaYaw % 1.0f == 0.0f && lastDeltaYaw % 1.0f == 0.0f || deltaPitch != 0.0f && deltaPitch % 1.0f == 0.0f && lastDeltaYaw % 1.0f == 0.0f || deltaPitch != 0.0f && deltaPitch % 1.0f == 0.0f && lastDeltaPitch % 1.0f == 0.0f;
            if (invalid && Math.abs(this.data.getRotationProcessor().getPitch()) != 90.0f) {
                //Abs(pitch) = 90 does weird stuff.
                if (this.increaseBuffer() > 3.5) {
                    this.fail();
                    this.setBuffer(3.0);
                }
            } else if (!invalid) {
                this.decreaseBufferBy(0.075);
            }
        }
    }
}
