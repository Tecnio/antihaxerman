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

package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Scaffold", type = "B", description = "Checks if player is not slowing down while moving head.")
public final class ScaffoldB extends Check {
    public ScaffoldB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double acceleration = deltaXZ - lastDeltaXZ;

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.VELOCITY, ExemptType.VEHICLE, ExemptType.SLIME, ExemptType.CLIMBABLE, ExemptType.PISTON, ExemptType.CHUNK, ExemptType.WEB, ExemptType.BOAT, ExemptType.UNDERBLOCK, ExemptType.FLYING, ExemptType.LIQUID);
            final boolean invalid = deltaYaw > 8.0F && acceleration >= 0.0 && data.getActionProcessor().isPlacing();

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
