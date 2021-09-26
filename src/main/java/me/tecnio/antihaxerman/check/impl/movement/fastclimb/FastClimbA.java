

package me.tecnio.antihaxerman.check.impl.movement.fastclimb;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "FastClimb", type = "A", description = "Checks if player is going faster than possible on a climbable.")
public final class FastClimbA extends Check {
    public FastClimbA(final PlayerData data) {
        super(data);
    }



    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double acceleration = deltaY - lastDeltaY;

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.PISTON, ExemptType.FLYING, ExemptType.BOAT, ExemptType.VEHICLE);
            final boolean invalid = ((float) deltaY) > 0.1176F && acceleration == 0.0 && data.getPositionProcessor().isOnClimbable();

            if (invalid && !exempt) {
                fail();
            }
        }
    }
}
