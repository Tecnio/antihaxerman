package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Aim", type = "J", description = "Checks for snappy rotations in the rotation packet.")
public class AimJ extends Check {

    private float lastDeltaYaw;
    private float lastLastDeltaYaw;

    public AimJ(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            if (this.isExempt(ExemptType.NEARHORSE, ExemptType.RESPAWN, ExemptType.TELEPORT, ExemptType.JOINED)) {
                return;
            }
            final float deltaYaw = this.data.getRotationProcessor().getDeltaYaw();
            if (deltaYaw < 5.0f && this.lastDeltaYaw > 30.0f && this.lastLastDeltaYaw < 5.0f) {
                final double low = (deltaYaw + this.lastLastDeltaYaw) / 2.0f;
                final double high = this.lastDeltaYaw;
                if(increaseBuffer() > 5) {
                    fail(String.format("low=%.2f, high=%.2f", low, high));
                } else {
                    decreaseBufferBy(0.10);
                }
            }
            this.lastLastDeltaYaw = this.lastDeltaYaw;
            this.lastDeltaYaw = deltaYaw;
        }
    }
}
