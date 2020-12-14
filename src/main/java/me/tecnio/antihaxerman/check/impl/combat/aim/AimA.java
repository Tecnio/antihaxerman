package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

@CheckInfo(name = "Aim", type = "A", description = "Checks for invalid rotation constant.")
public final class AimA extends Check {
    public AimA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && hitTicks() < 3) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final float lastDeltaYaw = data.getRotationProcessor().getLastDeltaYaw();
            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

            final double divisorYaw = MathUtil.getGcd((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));
            final double divisorPitch = MathUtil.getGcd((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

            final double constantYaw = divisorYaw / MathUtil.EXPANDER;
            final double constantPitch = divisorPitch / MathUtil.EXPANDER;

            final double currentX = deltaYaw / constantYaw;
            final double currentY = deltaPitch / constantPitch;

            final double previousX = lastDeltaYaw / constantYaw;
            final double previousY = lastDeltaPitch / constantPitch;

            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f) {
                final double moduloX = currentX % previousX;
                final double moduloY = currentY % previousY;

                final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                final boolean invalidX = moduloX > 90.d && floorModuloX > 0.1;
                final boolean invalidY = moduloY > 90.d && floorModuloY > 0.1;

                if (data.getRotationProcessor().isCinematic()) decreaseBufferBy(0.1);

                if (invalidX && invalidY) {
                    if (increaseBuffer() > 8) {
                        fail();
                    }
                } else {
                    decreaseBufferBy(0.25);
                }
            }
        }
    }
}
