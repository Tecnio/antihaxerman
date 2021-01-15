

package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

@CheckInfo(name = "Aim", type = "E", description = "Checks for bad GCD.", experimental = true)
public final class AimE extends Check {

    public AimE(final PlayerData data) {
        super(data);
    }

    // Again skidded from Elevated LOL

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && hitTicks() < 2) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final float lastDeltaYaw = data.getRotationProcessor().getLastDeltaYaw();
            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 30.d && deltaPitch < 20.d) {
                final long expandedYaw = (long) (deltaYaw * MathUtil.EXPANDER);
                final long previousExpandedYaw = (long) (lastDeltaYaw * MathUtil.EXPANDER);

                final long expandedPitch = (long) (deltaPitch * MathUtil.EXPANDER);
                final long previousExpandedPitch = (long) (lastDeltaPitch * MathUtil.EXPANDER);

                final double divisorPitch = MathUtil.getGcd(expandedPitch, previousExpandedPitch);
                final double divisorYaw = MathUtil.getGcd(expandedYaw, previousExpandedYaw);

                final boolean cinematic = data.getRotationProcessor().isCinematic();

                if (divisorYaw > 0.0 && divisorPitch > 0.0 && !cinematic) {
                    final double threshold = 131072;

                    if (divisorYaw < threshold || divisorPitch < threshold) {
                        final double deltaDivisor = Math.abs(divisorYaw - divisorPitch);

                        final boolean invalid = deltaDivisor > 700d;

                        if (invalid) {
                            if (increaseBuffer() > 10) {
                                fail();
                            }
                        }
                    } else {
                        resetBuffer();
                    }
                } else {
                    resetBuffer();
                }
            }
        }
    }
}
