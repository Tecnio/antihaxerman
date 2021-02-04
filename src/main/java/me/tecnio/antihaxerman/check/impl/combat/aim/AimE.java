/*
 *  Copyright (C) 2020 - 2021 Tecnio
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
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Aim", type = "E", description = "Checks for snappy rotations.", experimental = true)
public final class AimE extends Check {

    private float lastDeltaYaw, lastLastDeltaYaw;

    public AimE(final PlayerData data) {
        super(data);
    }

    // Credits to Medusa, by GladUrBad.

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean exempt = isExempt(ExemptType.TELEPORT_DELAY, ExemptType.TELEPORT);
            final boolean invalid = deltaYaw < 3.0F && lastDeltaYaw > 20F && lastLastDeltaYaw < 3.0F;

            if (invalid && !exempt) fail();

            lastLastDeltaYaw = lastDeltaYaw;
            lastDeltaYaw = deltaYaw;
        }
    }
}
