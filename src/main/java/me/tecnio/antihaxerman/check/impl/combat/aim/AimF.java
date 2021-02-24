/*
 *  Copyright (C) 2020-2021 Tecnio
 *
 *  This check is different than others, you can't take it or include it in any other application/project.
 *
 *  The license may allow you to use this check but in this scenario the license is not effective.
 *  And for anyone who opposes claiming license is GPLv3 I clearly have written a different license here.
 *
 *  Be aware.
 */

package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

@CheckInfo(name = "Aim", type = "F", description = "GCD bypass flaw detected (KEKW)")
public final class AimF extends Check {

    // Read the license above

    public AimF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && isExempt(ExemptType.COMBAT, ExemptType.BUKKIT_PLACING)) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float lastDeltaPitch = data.getRotationProcessor().getLastDeltaPitch();

            if (deltaPitch > 0.5) {
                final long expandedPitch = (long) (deltaPitch * MathUtil.EXPANDER);
                final long lastExpandedPitch = (long) (lastDeltaPitch * MathUtil.EXPANDER);

                final double divisorPitch = MathUtil.getGcd(expandedPitch, lastExpandedPitch);
                final double constantPitch = divisorPitch / MathUtil.EXPANDER;

                final double pitch = data.getRotationProcessor().getPitch();
                final double moduloPitch = Math.abs(pitch % constantPitch);

                if (moduloPitch < 1.0E-5) {
                    if (increaseBuffer() > 1) {
                        fail();
                    }
                } else {
                    decreaseBufferBy(0.01);
                }
            }
        }
    }
}
