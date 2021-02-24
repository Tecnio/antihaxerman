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

@CheckInfo(name = "Aim", type = "G", description = "GCD bypass flaw detected (KEKW)")
public final class AimG extends Check {

    public AimG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && isExempt(ExemptType.COMBAT, ExemptType.BUKKIT_PLACING)) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float lastDeltaYaw = data.getRotationProcessor().getLastDeltaYaw();

            if (deltaYaw > 0.5) {
                final long expandedYaw = (long) (deltaYaw * MathUtil.EXPANDER);
                final long lastExpandedYaw = (long) (lastDeltaYaw * MathUtil.EXPANDER);

                final double divisorYaw = MathUtil.getGcd(expandedYaw, lastExpandedYaw);
                final double constantYaw = divisorYaw / MathUtil.EXPANDER;

                final double yaw = data.getRotationProcessor().getYaw();
                final double moduloYaw = Math.abs(yaw % constantYaw);

                if (moduloYaw < 1.0E-5) {
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
