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

package me.tecnio.antihaxerman.manager;

import io.github.retrooper.packetevents.event.PacketListenerDynamic;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.event.priority.PacketEventPriority;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;


public final class NetworkManager extends PacketListenerDynamic {

    public NetworkManager() {
        super(PacketEventPriority.MONITOR);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());

        if (data != null) {
            AntiHaxerman.INSTANCE.getReceivingPacketProcessor()
                    .handle(data, new Packet(Packet.Direction.RECEIVE, event.getNMSPacket(), event.getPacketId()));
        }
    }

    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());

        if (data != null) {
            AntiHaxerman.INSTANCE.getSendingPacketProcessor()
                    .handle(data, new Packet(Packet.Direction.SEND, event.getNMSPacket(), event.getPacketId()));
        }
    }
}
