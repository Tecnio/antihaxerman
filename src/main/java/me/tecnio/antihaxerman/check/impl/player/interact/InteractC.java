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
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.GameMode;
import org.bukkit.util.Vector;

@CheckInfo(name = "Interact", type = "C", description = "Checks for block interaction distance.", experimental = true)
public final class InteractC extends Check {

    private WrappedPacketInBlockPlace wrapper = null;

    public InteractC(final PlayerData data) {
        super(data);
    }

    // You may ask are you retarded why are u subtracting deltas.
    // Well you can place blocks between ticks and position updates and you as well can move your head between ticks so this made sense idk tho it might be sleepiness that might be killing my brain.

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            if (data.getPlayer().getItemInHand().getType().isBlock()) {
                wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
            }
        } else if (packet.isFlying()) {
            if (wrapper != null) {
                final Vector eyeLocation = data.getPlayer().getEyeLocation().toVector();
                final Vector blockLocation = new Vector(
                        wrapper.getBlockPosition().getX(),
                        wrapper.getBlockPosition().getY(),
                        wrapper.getBlockPosition().getZ()
                );

                final double deltaXZ = Math.abs(data.getPositionProcessor().getDeltaXZ());
                final double deltaY = Math.abs(data.getPositionProcessor().getDeltaY());

                final double maxDistance = data.getPlayer().getGameMode() == GameMode.CREATIVE ? 7.25 : 5.25;
                final double distance = eyeLocation.distance(blockLocation) - 0.7071 - deltaXZ - deltaY;

                final boolean exempt = blockLocation.getX() == -1.0 && blockLocation.getY() == -1.0 && blockLocation.getZ() == -1.0;
                final boolean invalid = distance > maxDistance;

                if (invalid && !exempt) {
                    if (increaseBuffer() > 1) {
                        fail(distance);
                    }
                } else {
                    decreaseBufferBy(0.05);
                }
            }

            wrapper = null;
        }
    }
}
