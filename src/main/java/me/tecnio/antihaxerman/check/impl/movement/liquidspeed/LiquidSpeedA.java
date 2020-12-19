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

package me.tecnio.antihaxerman.check.impl.movement.liquidspeed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "LiquidSpeed", type = "A", description = "Predicts vertical motion in water.")
public final class LiquidSpeedA extends Check {
    public LiquidSpeedA(final PlayerData data) {
        super(data);
    }

    // So if anyone doesnt understand this, im just 'trying' to do the same calculation minecraft client does to predict next location.

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean inLiquid = data.getPositionProcessor().isFullySubmergedInLiquidStat();

            final double multiplier = data.getPositionProcessor().isInWater() ? 0.8 : 0.5;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double acceleration = deltaY - lastDeltaY;

            final double predicted = lastDeltaY * (multiplier) - 0.02F;
            final double difference = Math.abs(deltaY - predicted);

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.PISTON, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.WEB, ExemptType.SLIME, ExemptType.BOAT);
            final boolean invalid = difference > 0.001 && deltaY < 0.0 && acceleration <= 0.0 && inLiquid;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }
}
