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

@CheckInfo(name = "Interact", type = "E", description = "Checks for wrong interaction with a block.")
public final class InteractE extends Check {

    // All credits go to Hawk by Islandscout (https://github.com/HawkAnticheat/Hawk)
    // Check made by Islanscout and I need it bc bad friend bypass Scaffold.

    public InteractE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            final float x = wrapper.getCursorX();
            final float y = wrapper.getCursorY();
            final float z = wrapper.getCursorZ();

            for (final float value : new float[]{x, y, z}) {
                // The variable value cannot be larger than 1 or smaller than 0, as stated here.
                // https://wiki.vg/Protocol#Player_Block_Placement

                if (value > 1.0 || value < 0.0) fail();
            }
        }
    }
}
