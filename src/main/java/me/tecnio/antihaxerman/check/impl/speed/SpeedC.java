/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

package me.tecnio.antihaxerman.check.impl.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;

@CheckInfo(name = "Speed", type = "C")
public final class SpeedC extends Check {
    public SpeedC(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        double limit = PlayerUtils.getBaseSpeed(data.getPlayer());

        if (data.iceTicks() < 40 || data.slimeTicks() < 40) limit += 0.34;
        if (data.collidedVTicks() < 40) limit += 0.91;
        if (data.isTakingVelocity()) limit += Math.hypot(data.getLastVelocity().getX(), data.getLastVelocity().getZ());

        final boolean exempt = data.getPlayer().isInsideVehicle() || data.pistonTicks() < 10 || data.flyingTicks() < 20 || data.teleportTicks() < 20;

        if (data.getDeltaXZ() > limit && !exempt) {
            if (increaseBuffer() > 7) {
                flag("breached limit, s: " + data.getDeltaXZ());
            }
        } else {
            setBuffer(buffer * 0.75);
        }
    }
}
