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

package me.tecnio.antihaxerman.check.impl.movement.flight;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Flight", type = "A", description = "Flags flight's that don't obey gravity.")
public final class FlightA extends Check {
    public FlightA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int serverAirTicks = data.getPositionProcessor().getAirTicks();
            final int clientAirTicks = data.getPositionProcessor().getClientAirTicks();

            final int airTicksModifier = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP);
            final int airTicksLimit = 8 + airTicksModifier;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double predicted = (lastDeltaY - 0.08) * 0.9800000190734863;

            final double fixedPredicted = Math.abs(predicted) < 0.005 ? 0.0 : predicted;
            final double difference = Math.abs(deltaY - fixedPredicted);

            final double velocityY = data.getVelocityProcessor().getVelocityY();
            final double limit = isExempt(ExemptType.VELOCITY_ON_TICK) ? velocityY + 0.45 + 0.001 : 0.001;

            final boolean exempt = isExempt(ExemptType.PISTON, ExemptType.VEHICLE, ExemptType.TELEPORT,
                    ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING, ExemptType.WEB, ExemptType.JOINED,
                    ExemptType.SLIME, ExemptType.CLIMBABLE, ExemptType.CHUNK, ExemptType.VOID);
            final boolean invalid = difference > limit && (serverAirTicks > airTicksLimit || clientAirTicks > airTicksLimit);

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.1);
            }
        }
    }
}