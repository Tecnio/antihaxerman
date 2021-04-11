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

package me.tecnio.antihaxerman.check.impl.movement.jesus;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Jesus", type = "A", description = "Checks if player walking on liquids.")
public final class JesusA extends Check {
    public JesusA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            /*final List<Block> blocks = data.getPositionProcessor().getBlocks();
            final List<Block> blocksBelow = data.getPositionProcessor().getBlocksBelow();

            if (blocks == null || blocksBelow == null) return;

            final boolean containsLiquid = blocksBelow.stream().anyMatch(Block::isLiquid);
            final boolean doesntContainSolid = blocksBelow.stream().noneMatch(block -> block.getType().isSolid());

            final boolean liquidBelow = containsLiquid && doesntContainSolid;
            final boolean noBlocks = blocks.stream().anyMatch(block -> block.getType().isSolid() || (block.getType() != Material.AIR && !block.isLiquid()));

            final boolean clientGround = data.getPositionProcessor().isOnGround();
            final boolean serverGround = data.getPositionProcessor().isMathematicallyOnGround();

            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.CHUNK);
            final boolean invalid = (clientGround || serverGround) && liquidBelow && !noBlocks;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBuffer();
            }*/
        }
    }
}
