// Decompiled with: CFR 0.152
// Class Version: 8
package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name="Aim", type="E", description="Checks for consistent rotation deltas.")
public final class AimE
extends Check {
    private int streak = 0;
    private int streak2 = 0;
    private int streak3 = 0;
    private double lastabsyawaccel = 0.0;
    private double lastabspitchaccel = 0.0;

    public AimE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation() && this.data.getPositionProcessor().getSinceTeleportTicks() > 2 && Math.abs(this.data.getRotationProcessor().getPitch()) != 90.0f) {
            float deltaYaw = this.data.getRotationProcessor().getDeltaYaw();
            float deltaPitch = this.data.getRotationProcessor().getDeltaPitch();
            float lastDeltaYaw = this.data.getRotationProcessor().getLastDeltaYaw();
            float lastDeltaPitch = this.data.getRotationProcessor().getLastDeltaPitch();
            if ((double)(deltaYaw + deltaPitch) < 2.5 || (double)(lastDeltaYaw + lastDeltaPitch) < 2.5) {
                return;
            }
            if (this.data.getRotationProcessor().getSensitivity() > 140 && (this.data.getRotationProcessor().getMouseDeltaX() < 2 || this.data.getRotationProcessor().getMouseDeltaX() < 2)) {
                return;
            }
            if (Math.abs(deltaYaw) == Math.abs(lastDeltaYaw) || Math.abs(deltaPitch) == Math.abs(lastDeltaPitch) || Math.abs(deltaYaw) == Math.abs(lastDeltaPitch) || Math.abs(deltaPitch) == Math.abs(lastDeltaYaw)) {
                if (this.streak3++ > 5 && this.increaseBuffer() > 9.0) {
                    this.fail("Weird Rotations: " + this.streak3);
                }
            } else {
                this.decreaseBufferBy(0.05);
                this.streak3 = 0;
            }
            if (deltaYaw == lastDeltaYaw || deltaPitch == lastDeltaPitch || deltaYaw == deltaPitch) {
                if (this.streak++ > 4 && this.increaseBuffer() > 5.0) {
                    this.fail("Static Rotations: " + this.streak);
                }
            } else {
                this.decreaseBufferBy(0.1);
                this.streak = 0;
            }
            double absyawaccel = Math.abs(deltaYaw - lastDeltaYaw);
            double abspitchaccel = Math.abs(deltaPitch - lastDeltaPitch);
            if (abspitchaccel == 0.0 || absyawaccel == 0.0 || abspitchaccel == absyawaccel || abspitchaccel == this.lastabspitchaccel || this.lastabsyawaccel == absyawaccel) {
                if (this.streak2++ > 5 && this.increaseBuffer() > 6.0) {
                    this.fail("Weird Acceleration: " + this.streak2);
                    this.setBuffer(6.0);
                }
            } else {
                this.decreaseBufferBy(0.1);
                this.streak2 = 0;
            }
        }
    }
}
