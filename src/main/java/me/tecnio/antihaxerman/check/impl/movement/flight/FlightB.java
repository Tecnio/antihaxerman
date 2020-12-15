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

package me.tecnio.antihaxerman.check.impl.movement.flight;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Flight", type = "B", description = "Checks for the vertical acceleration of the player.")
public final class FlightB extends Check {
    public FlightB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        final double deltaY = data.getPositionProcessor().getDeltaY();

        final int airTicksModifier = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP);
        final int airTicksLimit = 6 + airTicksModifier;

        final int airTicks = data.getPositionProcessor().getAirTicks();

        final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.PISTON, ExemptType.VEHICLE, ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING, ExemptType.WEB, ExemptType.SLIME, ExemptType.CLIMBABLE);
        final boolean invalid = airTicks > airTicksLimit && deltaY > 0.0;

        if (invalid && !exempt) {
            if (increaseBuffer() > 2) {
                fail();
            }
        } else {
            decreaseBufferBy(0.01);
        }
    }
}
