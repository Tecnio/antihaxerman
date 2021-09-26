

package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Scaffold", type = "B", description = "Checks if player is not slowing down while moving head.")
public final class ScaffoldB extends Check {

    private boolean placing;

    public ScaffoldB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double acceleration = deltaXZ - lastDeltaXZ;

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.VELOCITY, ExemptType.VEHICLE, ExemptType.SLIME, ExemptType.CLIMBABLE, ExemptType.PISTON, ExemptType.CHUNK, ExemptType.WEB, ExemptType.BOAT, ExemptType.UNDERBLOCK, ExemptType.FLYING, ExemptType.LIQUID);
            final boolean invalid = deltaYaw > 10.0F && acceleration >= -1.0E-2 && placing;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                decreaseBuffer();
            }

            placing = false;
        } else if (packet.isBukkitBlockPlace()) {
            placing = true;
        }
    }
}
