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

package me.tecnio.antihaxerman.check.impl.autoblock;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

@CheckInfo(name = "AutoBlock", type = "A")
public final class AutoBlockA extends Check {
    public AutoBlockA(PlayerData data) {
        super(data);
    }

    private boolean attacked;
    private int ticks;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.USE_ENTITY) {
            attacked = true;
        } else if (event.getPacketId() == PacketType.Client.BLOCK_PLACE) {
            if (attacked) {
                if (ticks < 2) {
                    if (increaseBuffer() > 2) {
                        flag();
                    }
                } else {
                    resetBuffer();
                }
                attacked = false;
            }

            ticks = 0;
        } else if (PacketUtils.isFlyingPacket(event.getPacketId())) {
            ticks++;
        }
    }
}
