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

package me.tecnio.antihaxerman.check.impl.player.interact;

import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.BlockUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;

@CheckInfo(name = "Interact", type = "B", description = "Checks if player is interacting with a side of block which is not visible.")
public final class InteractB extends Check {
    public InteractB(final PlayerData data) {
        super(data);
    }

    // Directly skidded from GladUrBad/Medusa literally hardcore skidded no shit but if I get dmca'ed I wouldn't be surprised.
    // But idc check too good lol.

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            final Location blockLocation = new Location(data.getPlayer().getWorld(),
                    wrapper.getBlockPosition().getX(),
                    wrapper.getBlockPosition().getY(),
                    wrapper.getBlockPosition().getZ()
            );
            final Direction direction = wrapper.getDirection();

            final Block block = BlockUtil.getBlockAsync(blockLocation);
            if (block == null) return;

            if (block.getType().isSolid()) {
                final double x = data.getPositionProcessor().getX();
                final double y = data.getPositionProcessor().getY();
                final double z = data.getPositionProcessor().getZ();

                if ((y - block.getX()) > 0.45) {
                    final Location location = new Location(data.getPlayer().getWorld(), x, y + data.getPlayer().getEyeHeight(), z);

                    final boolean invalid = !interactedCorrectly(blockLocation, location, direction);

                    if (invalid) {
                        if (increaseBuffer() > 1) {
                            fail();
                        }
                    } else {
                        resetBuffer();
                    }
                }
            }
        }
    }

    private boolean interactedCorrectly(final Location blockLoc, final Location playerLoc, final Direction face) {
        switch (face) {
            case UP: {
                //final double limit = blockLoc.getY() + 0.03;
                return true;/*playerLoc.getY() > limit;*/
            }
            case DOWN: {
                final double limit = blockLoc.getY() - 0.03;
                return playerLoc.getY() < limit;
            }
            case WEST: {
                final double limit = blockLoc.getX() + 0.03;
                return limit > playerLoc.getX();
            }
            case EAST: {
                final double limit = blockLoc.getX() - 0.03;
                return playerLoc.getX() > limit;
            }
            case NORTH: {
                final double limit = blockLoc.getZ() + 0.03;
                return playerLoc.getZ() < limit;
            }
            case SOUTH: {
                final double limit = blockLoc.getZ() - 0.03;
                return playerLoc.getZ() > limit;
            }

            default: return true;
        }
    }
}
