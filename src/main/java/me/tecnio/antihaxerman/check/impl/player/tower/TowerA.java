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

package me.tecnio.antihaxerman.check.impl.player.tower;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.Location;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Tower", type = "A", description = "Checks if player is towering up too fast.")
public final class TowerA extends Check {

    private Location lastLocation = new Location(null, -69, -69, -69);
    private int ticksSincePlace;

    public TowerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBukkitBlockPlace()) {
            final BlockPlaceEvent event = (BlockPlaceEvent) packet.getRawPacket().getRawNMSPacket();

            final Location location = event.getBlock().getLocation();
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final boolean invalid = ticksSincePlace < 7 && placementUnder(location) && deltaY > 0.0;

            if (invalid) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.50);
            }

            lastLocation = location;
            ticksSincePlace = 0;
        } else if (packet.isFlying()) {
            ++ticksSincePlace;
        }
    }

    private boolean placementUnder(final Location blockLocation) {
        final double x = data.getPositionProcessor().getX();
        final double y = data.getPositionProcessor().getY();
        final double z = data.getPositionProcessor().getZ();

        final double blockX = blockLocation.getX();
        final double blockY = blockLocation.getY();
        final double blockZ = blockLocation.getZ();

        final double lastBlockY = lastLocation.getY();

        return Math.floor(y - 0.25) == blockY
                && blockY < y
                && lastBlockY < y
                && lastBlockY < blockY
                && Math.abs(x - blockX) <= 0.8
                && Math.abs(z - blockZ) <= 0.8;
    }
}
