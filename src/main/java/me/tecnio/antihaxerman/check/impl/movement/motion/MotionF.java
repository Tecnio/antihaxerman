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

package me.tecnio.antihaxerman.check.impl.movement.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Motion", type = "F", description = "Detects slight air modifications.")
public final class MotionF extends Check {

    // This is basically Flight A you may ask why I added this which is a fair question.
    // I added this because this is bandaid fixed version of that check and can flag slight modifications.
    // With the cost of being more bypassable.

    public MotionF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean clientAir = data.getPositionProcessor().getClientAirTicks() > 1;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final boolean notUnderBlock = data.getPositionProcessor().getSinceBlockNearHeadTicks() > 5;

            final double predicted = (lastDeltaY - 0.08) * 0.98F;
            final double difference = Math.abs(deltaY - predicted);

            final boolean exempt = isExempt(ExemptType.PISTON, ExemptType.VEHICLE, ExemptType.TELEPORT_DELAY_SMALL,
                    ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING, ExemptType.WEB, ExemptType.JOINED, ExemptType.VELOCITY,
                    ExemptType.SLIME_ON_TICK, ExemptType.CLIMBABLE, ExemptType.CHUNK, ExemptType.VOID, ExemptType.CHUNK_CLIENT_SPF);
            final boolean invalid = difference > 1.0E-4 && Math.abs(predicted) > 0.005 && clientAir && notUnderBlock;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.01);
            }
        }
    }
}
