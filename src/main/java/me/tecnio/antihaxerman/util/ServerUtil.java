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

package me.tecnio.antihaxerman.util;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServerUtil {

    public double getTPS() {
        //return Math.min(20.0, PacketEvents.get().getServerUtils().getTPS());
        return 20.0D;
    }

    public ServerVersion getServerVersion() {
        return PacketEvents.get().getServerUtils().getVersion();
    }

    public boolean isLowerThan1_8() {
        return getServerVersion().isLowerThan(ServerVersion.v_1_8);
    }
}
