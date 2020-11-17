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

@CheckInfo(name = "Speed", type = "D")
public final class SpeedD extends Check {
    public SpeedD(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        double limit = data.sprintingTicks() < 20 ? PlayerUtils.getBaseSpeed(data.getPlayer(), 0.305F) : PlayerUtils.getBaseSpeed(data.getPlayer(), 0.23F);

        if (data.iceTicks() < 40 || data.slimeTicks() < 40) limit += 0.34;
        if (data.collidedVTicks() < 40) limit += 0.7;
        if (data.isTakingVelocity()) limit += Math.hypot(data.getLastVelocity().getX(), data.getLastVelocity().getZ());

        final boolean exempt = data.getPlayer().isInsideVehicle() || data.flyingTicks() < 20 || data.pistonTicks() < 10 || data.teleportTicks() < 20;
        final boolean invalid = data.getGroundTicks() >= 15 && data.getDeltaXZ() > limit;

        if (invalid && !exempt) {
            if (increaseBuffer() > 2) {
                flag();
            }
        } else {
            resetBuffer();
        }
    }
}
