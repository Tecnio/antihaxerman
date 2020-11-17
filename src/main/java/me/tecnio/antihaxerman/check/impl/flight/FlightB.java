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

package me.tecnio.antihaxerman.check.impl.flight;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.CollisionUtils;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Flight", type = "B")
public final class FlightB extends Check {
    public FlightB(PlayerData data) {
        super(data);
    }

    @Override
    public void onFlying() {
        final double limit = 6 + (PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.JUMP));

        final boolean exempt = data.getPlayer().isFlying() || data.pistonTicks() < 10 || data.liquidTicks() < 10 || data.isOnGround() || data.isNearBoat() || CollisionUtils.isOnGround(data) || data.climbableTicks() < 10 || data.getPlayer().getVelocity().getY() >= -0.075 || data.isTakingVelocity() || data.getPlayer().isInsideVehicle() || data.teleportTicks() < 20 || !data.getLocation().getBlock().getType().toString().equalsIgnoreCase("AIR");
        final boolean invalid = data.getDeltaY() > 0.0 && data.getAirTicks() > limit;

        if (invalid && !exempt) {
            if (increaseBuffer() > 2) {
                flag();
            }
        } else {
            decreaseBufferBy(0.1);
        }
    }
}
