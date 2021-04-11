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

@CheckInfo(name = "Flight", type = "A", description = "Flags flight's that don't obey gravity.")
public final class FlightA extends Check {
    public FlightA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double velocityY = data.getVelocityProcessor().getVelocityY();

            final int serverAirTicks = data.getPositionProcessor().getAirTicks();
            final int clientAirTicks = data.getPositionProcessor().getClientAirTicks();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double predicted = (lastDeltaY - 0.08) * 0.9800000190734863;

            double fixedPredicted = Math.abs(predicted) < 0.005 ? -0.08 * 0.9800000190734863 : predicted;
            if (isExempt(ExemptType.VELOCITY_ON_TICK)) fixedPredicted = velocityY;

            final double difference = Math.abs(deltaY - fixedPredicted);
            final double limit = 1.0E-4;

            final boolean exempt = isExempt(ExemptType.PISTON, ExemptType.VEHICLE, ExemptType.TELEPORT,
                    ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING, ExemptType.WEB, ExemptType.JOINED,
                    ExemptType.SLIME_ON_TICK, ExemptType.CLIMBABLE, ExemptType.CHUNK, ExemptType.VOID, ExemptType.UNDERBLOCK);
            final boolean invalid = difference > limit && (serverAirTicks > 8 || clientAirTicks > 8);

            if (invalid && !exempt) {
                if (increaseBuffer() > 3) {
                    fail(String.format("pred: %.4f delta: %.4f vel: %s", fixedPredicted, deltaY, isExempt(ExemptType.VELOCITY_ON_TICK)));
                }
            } else {
                decreaseBufferBy(0.15);
            }
        }
    }
}