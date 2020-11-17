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

package me.tecnio.antihaxerman.manager;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.out.transaction.WrappedPacketOutTransaction;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

public final class TransactionManager {

    public static void onPacketReceive(PacketReceiveEvent event) {
        PlayerData data = PlayerDataManager.getPlayerData().get(event.getPlayer().getUniqueId());
        if (data != null) {
            if (PacketUtils.isFlyingPacket(event.getPacketId())) {
                short id = (short) data.getRandom().nextInt(32767);
                id = data.getVelocityID() == id ? (short) (id - 1) : id;

                PacketEvents.getAPI().getPlayerUtils().sendPacket(data.getPlayer(), new WrappedPacketOutTransaction(0, id, false));

            } else if (event.getPacketId() == PacketType.Client.TRANSACTION) {
                final WrappedPacketInTransaction wrapper = new WrappedPacketInTransaction(event.getNMSPacket());

                final long now = System.currentTimeMillis();

                data.getTransactionUpdates().computeIfPresent(wrapper.getActionNumber(), (id, time) -> {
                    data.setTransactionPing((int) (now - time));
                    data.getTransactionUpdates().remove(id);

                    return time;
                });
            }
        }
    }

    public static void onPacketSend(PacketSendEvent event) {
        PlayerData data = PlayerDataManager.getPlayerData().get(event.getPlayer().getUniqueId());
        if (data != null) {
            if (event.getPacketId() == PacketType.Server.TRANSACTION) {
                final WrappedPacketOutTransaction wrapper = new WrappedPacketOutTransaction(event.getNMSPacket());

                data.getTransactionUpdates().put(wrapper.getActionNumber(), System.currentTimeMillis());
            }
        }
    }

}
