

package me.tecnio.antihaxerman.check.impl.movement.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Motion", type = "C", description = "Checks for repeated and impossible vertical motion.")
public final class MotionC extends Check {
    public MotionC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.NEAR_WALL, ExemptType.NEARANVIL, ExemptType.WEB, ExemptType.PEARL, ExemptType.UNDERBLOCK, ExemptType.PISTON, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.CHUNK, ExemptType.VEHICLE, ExemptType.BOAT);
            final boolean invalid = deltaY == -lastDeltaY && deltaY != 0.0;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                resetBuffer();
            }
        }
    }
}
