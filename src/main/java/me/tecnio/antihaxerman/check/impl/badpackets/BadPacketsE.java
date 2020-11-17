/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

package me.tecnio.antihaxerman.check.impl.badpackets;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.steervehicle.WrappedPacketInSteerVehicle;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "BadPackets", type = "E", maxVL = 1)
public final class BadPacketsE extends Check {
    public BadPacketsE(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.STEER_VEHICLE) {
            final WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(event.getNMSPacket());

            if (data.getPlayer().getVehicle() == null) {
                flag();
            }

            final float forward = wrapper.getForwardValue();
            final float sideways = wrapper.getSideValue();

            if (forward != 0.0F && forward != 0.98F) {
                flag();
            }

            if (sideways != 0.0F && sideways != 0.98F) {
                flag();
            }
        }
    }
}
