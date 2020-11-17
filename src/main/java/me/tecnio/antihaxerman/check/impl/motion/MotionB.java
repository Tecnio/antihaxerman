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

package me.tecnio.antihaxerman.check.impl.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "B")
public final class MotionB extends Check {
    public MotionB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final double max = 0.7 + PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1;
        final double deltaY = data.getDeltaY();

        final boolean exempt = data.getPlayer().getVelocity().getY() >= -0.075 || data.pistonTicks() < 10 || data.liquidTicks() < 20 || data.flyingTicks() < 20 || data.isInWeb() || data.teleportTicks() < 20;

        if (deltaY > max && !data.isTakingVelocity() && !exempt) {
            flag();
        } else if (deltaY > (max + data.getLastVelocity().getY()) && !exempt) {
            flag();
        }
    }
}
