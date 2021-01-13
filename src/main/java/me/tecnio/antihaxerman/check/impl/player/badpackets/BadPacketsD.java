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

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle.WrappedPacketInSteerVehicle;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.entity.Entity;

import java.util.List;

@CheckInfo(name = "BadPackets", type = "D", description = "Detects steer vehicle disabler.")
public final class BadPacketsD extends Check {
    public BadPacketsD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isSteerVehicle()) {
            final WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(packet.getRawPacket());

            handle: {
                if (data.getPlayer().getVehicle() == null) {
                    final List<Entity> nearbyEntities = data.getPositionProcessor().getNearbyEntities();
                    if (nearbyEntities == null) break handle;

                    if (nearbyEntities.isEmpty()) {
                        ban();
                    }
                }
            }

            final float forward = Math.abs(wrapper.getForwardValue());
            final float sideways = Math.abs(wrapper.getSideValue());

            if (forward != 0.0F && forward != 0.98F) {
                ban();
            }

            if (sideways != 0.0F && sideways != 0.98F) {
                ban();
            }
        }
    }
}
