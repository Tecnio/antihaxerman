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

package me.tecnio.antihaxerman.check.impl.movement.fastclimb;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

@CheckInfo(name = "FastClimb", type = "B", description = "Checks if player is going faster than possible on a climbable.")
public final class FastClimbB extends Check {
    public FastClimbB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int sinceGround = data.getPositionProcessor().getClientAirTicks();

            final List<Block> blocks = data.getPositionProcessor().getBlocks();
            if (blocks == null) return;

            final boolean onClimbable = blocks.stream().anyMatch(block -> block.getType() == Material.LADDER || block.getType() == Material.VINE);

            final float deltaY = (float) data.getPositionProcessor().getDeltaY();
            final float limit = 0.1176F;

            final float groundLimit = sinceGround < 4 ? 0.42F / (sinceGround / 2.0F) : 0.0F;

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.PISTON, ExemptType.FLYING, ExemptType.BOAT, ExemptType.VEHICLE);
            final boolean invalid = deltaY > (limit + groundLimit) && onClimbable;

            if (invalid && !exempt) {
                if (increaseBuffer() > 3 || deltaY > (limit * 5.0F)) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.1);
            }
        }
    }
}
