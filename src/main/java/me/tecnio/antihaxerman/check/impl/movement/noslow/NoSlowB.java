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

package me.tecnio.antihaxerman.check.impl.movement.noslow;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "NoSlow", type = "B", description = "Checks if player is sneaking and sprinting.")
public final class NoSlowB extends Check {
    public NoSlowB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int groundTicks = data.getPositionProcessor().getGroundTicks();

            final int sprintingTicks = data.getActionProcessor().getSprintingTicks();
            final int sneakingTicks = data.getActionProcessor().getSneakingTicks();

            final boolean exempt = isExempt(ExemptType.CHUNK) || groundTicks < 10;
            final boolean invalid = sprintingTicks > 10 && sneakingTicks > 10;

            if (invalid && !exempt) {
                if (increaseBuffer() > 10) {
                    fail();
                }
            } else {
                resetBuffer();
            }
        }
    }
}
