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

package me.tecnio.antihaxerman.check.impl.omnisprint;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@CheckInfo(name = "OmniSprint", type = "A")
public final class OmniSprintA extends Check {
    public OmniSprintA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final Vector move = new Vector(data.getDeltaX(), 0, data.getDeltaZ());
        final double delta = move.distanceSquared(data.getDirection());

        final boolean exempt = data.getDeltaXZ() < 0.1 || data.liquidTicks() < 20 || data.isInWeb() || data.isTakingVelocity() || !data.isOnGround();
        final boolean invalid = delta >= getLimit() && data.isSprinting();

        if (invalid && !exempt) {
            if (increaseBuffer() > 4) {
                flag();
            }
        } else {
            resetBuffer();
        }
    }

    private double getLimit() {
        return data.getPlayer().getWalkSpeed() > 0.2f ? .23 * 1 + ((data.getPlayer().getWalkSpeed() / 0.2f) * 0.36) : 0.23 + (PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.062f);
    }
}
