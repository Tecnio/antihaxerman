package me.tecnio.antihaxerman.checks.impl.combat.aim;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.MathUtils;

@CheckInfo(name = "Aim", type = "A")
public class AimA extends Check {
    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if (isRotationPacket(e)) {
            // Get the deltas from the rotation update
            final float deltaYaw = data.getDeltaYaw();
            final float deltaPitch = data.getDeltaPitch();
            final float lastDeltaYaw = data.getLastDeltaYaw();
            final float lastDeltaPitch = data.getLastDeltaPitch();

            // Grab the gcd using an expander.
            final double divisorYaw = MathUtils.getGcd((long) (deltaYaw * MathUtils.EXPANDER), (long) (lastDeltaYaw * MathUtils.EXPANDER));
            final double divisorPitch = MathUtils.getGcd((long) (deltaPitch * MathUtils.EXPANDER), (long) (lastDeltaPitch * MathUtils.EXPANDER));

            // Get the constant for both rotation updates by dividing by the expander
            final double constantYaw = divisorYaw / MathUtils.EXPANDER;
            final double constantPitch = divisorPitch / MathUtils.EXPANDER;

            // Get the estimated mouse delta from the constant
            final double currentX = deltaYaw / constantYaw;
            final double currentY = deltaPitch / constantPitch;

            // Get the estimated mouse delta from the old rotations using the new constant
            final double previousX = lastDeltaYaw / constantYaw;
            final double previousY = lastDeltaPitch / constantPitch;

            // Make sure the rotation is not very large and not equal to zero and get the modulo of the xys
            if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f) {
                final double moduloX = currentX % previousX;
                final double moduloY = currentY % previousY;

                // Get the floor delta of the the moduloes
                final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                // Impossible to have a different constant in two rotations
                final boolean invalidX = moduloX > 90.d && floorModuloX > 0.1;
                final boolean invalidY = moduloY > 90.d && floorModuloY > 0.1;

                if (invalidX && invalidY) {
                    if (++preVL > 6) {
                        flag(data, "invalid rotations.");
                    }
                } else preVL = 0;
            }
        }
    }
}
