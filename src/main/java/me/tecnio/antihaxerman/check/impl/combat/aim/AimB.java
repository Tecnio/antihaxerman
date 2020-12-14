package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

@CheckInfo(name = "Aim", type = "B", description = "Checks for smaller gcd than possible.")
public final class AimB extends Check {
    public AimB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && hitTicks() < 3) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

            final boolean cinematic = data.getRotationProcessor().isCinematic();

            final long gcd = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));
            final boolean invalid = gcd < 131072L && deltaPitch > 0.5F && deltaPitch < 20.0F && !cinematic;

            if (invalid) {
                if (increaseBuffer() > 6) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.5);
            }
        }
    }
}
