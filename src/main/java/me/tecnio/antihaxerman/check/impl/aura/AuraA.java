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

package me.tecnio.antihaxerman.check.impl.aura;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@CheckInfo(name = "Aura", type = "A")
public final class AuraA extends Check {
    public AuraA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final Entity target = data.getTarget();

        final double accel = Math.abs(data.getDeltaXZ() - data.getLastDeltaXZ());

        final boolean exempt = !(target instanceof Player);
        final boolean invalid = accel < 0.0027 && data.isSprinting() && data.getDeltaXZ() > 0.22 && data.attackTicks() < 2;

        if (invalid && !exempt) {
            if (increaseBuffer() > 4) {
                flag();
            }
        } else {
            decreaseBufferBy(0.1);
        }
    }
}
