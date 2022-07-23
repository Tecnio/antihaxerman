// Decompiled with: CFR 0.152
// Class Version: 8
package me.tecnio.antihaxerman.check.impl.movement.omnisprint;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;

@CheckInfo(name="Speed", type="G", description="Checks for moving faster than possible without sprinting.")
public final class SpeedG
extends Check {

    public SpeedG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            boolean sprinting = this.data.getActionProcessor().isSprinting();
            double deltaXZ = this.data.getPositionProcessor().getDeltaXZ();
            double lastDeltaXZ = this.data.getPositionProcessor().getLastDeltaXZ();
            double deltaY = this.data.getPositionProcessor().getDeltaY();
            double groundLimit = PlayerUtil.getBaseGroundSpeed(this.data.getPlayer()) * 0.8;
            boolean exempt = this.isExempt(ExemptType.VELOCITY, ExemptType.CHUNK, ExemptType.JOINED, ExemptType.UNDERBLOCK, ExemptType.ICE, ExemptType.LIQUID);
            boolean invalid = !sprinting && this.data.getPositionProcessor().getGroundTicks() >= 5 && deltaXZ > groundLimit && Math.abs(deltaY) < 0.03;
            if (!exempt && deltaXZ > 0.03 && groundLimit > 0.03 && invalid && this.data.getPositionProcessor().getSinceTeleportTicks() > 4) {
                if (increaseBuffer() > 4.0) {
                    this.fail("GroundTicks: " + this.data.getPositionProcessor().getGroundTicks() + " DeltaXZ: " + deltaXZ + " Limit: " + groundLimit);
                    setBuffer(3.5);
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
