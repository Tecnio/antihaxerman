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

package me.tecnio.antihaxerman.check.impl.movement.jesus;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.block.Block;

import java.util.List;

@CheckInfo(name = "Jesus", type = "B", description = "Checks if player is walking water.")
public final class JesusB extends Check {
    public JesusB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final List<Block> blocksBelow = data.getPositionProcessor().getBlocksBelow();
            final List<Block> blocksAbove = data.getPositionProcessor().getBlocksAbove();

            if (blocksBelow == null || blocksAbove == null) return;

            final boolean liquidBelow = blocksBelow.stream().allMatch(Block::isLiquid);
            final boolean noLiquidAbove = blocksAbove.stream().noneMatch(Block::isLiquid);

            final boolean fullySubmerged = data.getPositionProcessor().isFullySubmergedInLiquidStat();

            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.VEHICLE, ExemptType.VELOCITY, ExemptType.FLYING, ExemptType.UNDERBLOCK);
            final boolean invalid = Math.abs(deltaY) < 0.0001 && liquidBelow && noLiquidAbove && !fullySubmerged;

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
