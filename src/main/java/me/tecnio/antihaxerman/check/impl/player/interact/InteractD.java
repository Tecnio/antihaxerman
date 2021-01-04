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

package me.tecnio.antihaxerman.check.impl.player.interact;

import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@CheckInfo(name = "Interact", type = "D", description = "Checks if player is looking at the block interacted.")
public final class InteractD extends Check {

    private WrappedPacketInBlockPlace wrapper = null;

    public InteractD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
        } else if (packet.isFlying()) {
            if (wrapper != null) {
                final Location eyeLocation = data.getPlayer().getEyeLocation();
                final Location blockLocation = new Location(data.getPlayer().getWorld(), wrapper.getX(), wrapper.getY(), wrapper.getZ());

                final double difference = getDifference(eyeLocation, blockLocation);
                // Stole the line down below from my first anticheat, its probabbly skidded so if I skidded from you sorry xd.
                final double angle = Math.abs(180 - Math.abs(Math.abs(difference - data.getRotationProcessor().getYaw()) - 180));

                final boolean exempt = blockLocation.getX() == -1.0 && blockLocation.getY() == -1.0 && blockLocation.getZ() == -1.0;
                final boolean invalid = angle > 100.0F;

                if (invalid && !exempt) {
                    if (increaseBuffer() > 3) {
                        fail();
                    }
                } else {
                    decreaseBuffer();
                }
            }

            wrapper = null;
        }
    }

    private double getDifference(final Location location1, final Location location2) {
        final Location directionLocation = location1.clone();

        final Vector origin = location1.toVector();
        final Vector target = location2.toVector();

        return directionLocation.setDirection(target.subtract(origin)).getYaw();
    }
}
