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

package me.tecnio.antihaxerman.utils.packet;

import lombok.experimental.UtilityClass;

import static io.github.retrooper.packetevents.packettype.PacketType.Client;

@UtilityClass
public final class PacketUtils {
    public boolean isFlyingPacket(byte type) {
        return type == Client.FLYING || type == Client.POSITION || type == Client.POSITION_LOOK || type == Client.LOOK;
    }

    public boolean isPositionPacket(byte type) {
        return type == Client.POSITION || type == Client.POSITION_LOOK || type == Client.LOOK;
    }

    public boolean isRotationPacket(byte type) {
        return type == Client.POSITION_LOOK || type == Client.LOOK;
    }
}
