/*
 *  Copyright (C) 2020 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

@CheckInfo(name = "Aim", type = "F", description = "Checks for unlikely and Aura like rotations.")
public final class AimF extends Check {
    public AimF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation() && hitTicks() < 3) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

            final boolean deltaYawSmall = MathUtil.isExponentiallySmall(deltaYaw);
            final boolean deltaPitchSmall = MathUtil.isExponentiallySmall(deltaPitch);

            final boolean invalid = (deltaPitchSmall && deltaYaw > 5.0F) || (deltaYawSmall && deltaPitch > 5.0F);

            if (invalid) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
