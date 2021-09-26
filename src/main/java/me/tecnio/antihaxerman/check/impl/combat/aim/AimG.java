

package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

@CheckInfo(name = "Aim", type = "G", description = "GCD bypass flaw detected (KEKW)")
public final class AimG extends Check {

    public AimG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && isExempt(ExemptType.COMBAT, ExemptType.PLACING)) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

            if (deltaPitch > 1.0 && !data.getPositionProcessor().isTeleported()) {
                final long expandedPitch = (long) (deltaPitch * MathUtil.EXPANDER);
                final long lastExpandedPitch = (long) (lastDeltaPitch * MathUtil.EXPANDER);

                final double divisorPitch = MathUtil.getGcd(expandedPitch, lastExpandedPitch);
                final double constantPitch = divisorPitch / MathUtil.EXPANDER;

                final double pitch = data.getRotationProcessor().getPitch();
                final double moduloPitch = Math.abs(pitch % constantPitch);

                if (moduloPitch < 1.2E-5) {
                    if (increaseBuffer() > 2) {
                        fail();
                    }
                } else {
                    decreaseBufferBy(0.05);
                }
            }
        }
    }
}
