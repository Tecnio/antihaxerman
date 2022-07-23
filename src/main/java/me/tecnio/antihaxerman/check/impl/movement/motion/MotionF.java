package me.tecnio.antihaxerman.check.impl.movement.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name="Motion", type="F", description="Checks for invalid direction of travel.")
public final class MotionF
extends Check {

    public MotionF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            float deltaYaw = this.data.getRotationProcessor().getDeltaYaw();
            double deltaXZ = this.data.getPositionProcessor().getDeltaXZ();
            double lastDeltaXZ = this.data.getPositionProcessor().getLastDeltaXZ();
            double Acceleration = Math.abs(deltaXZ - lastDeltaXZ) * 100.0;
            //When yaw is changed direction of travel should change.
            boolean exempt = this.isExempt(ExemptType.TELEPORT_DELAY, ExemptType.TELEPORT, ExemptType.FLYING, ExemptType.WEB, ExemptType.VEHICLE, ExemptType.LIQUID, ExemptType.CHUNK, ExemptType.JOINED);
            boolean invalid = Acceleration < 2.0E-5;
            if (invalid && this.data.getPositionProcessor().getGroundTicks() > 2) {
                if (deltaYaw > 1.0f && deltaXZ > 0.03 && lastDeltaXZ > 0.03 && this.increaseBuffer() > 1.0) {
                    this.fail("Acceleration: " + Acceleration);
                }
            } else {
                this.decreaseBufferBy(2.5E-4);
            }
        }
    }
}
