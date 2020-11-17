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

package me.tecnio.antihaxerman.check.impl.pingspoof;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.out.keepalive.WrappedPacketOutKeepAlive;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

import java.util.HashMap;
import java.util.Map;

@CheckInfo(name = "PingSpoof" , type = "A", autoBan = false)
public final class PingSpoofA extends Check {
    public PingSpoofA(PlayerData data) {
        super(data);
    }

    private final Map<Long, Long> keepAliveUpdates = new HashMap<>();
    private int keepAlivePing;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.KEEP_ALIVE) {
            final WrappedPacketInKeepAlive wrapper = new WrappedPacketInKeepAlive(event.getNMSPacket());

            final long now = System.currentTimeMillis();

            keepAliveUpdates.computeIfPresent(wrapper.getId(), (id, time) -> {
                keepAlivePing = (int) (now - time);
                keepAliveUpdates.remove(id);

                return time;
            });

            final int transactionPing = data.getTransactionPing();
            final int keepAlivePing = this.keepAlivePing;

            final int diff = Math.abs(transactionPing - keepAlivePing);

            final boolean exempt = data.isLagging() || data.getTick() < 100 || data.teleportTicks() < 100 || !data.getPlayer().getLocation().getChunk().isLoaded() || PacketEvents.getAPI().getServerUtils().getTPS() < 18;

            if (diff > 100 && !exempt) {
                if (increaseBuffer() > 30) {
                    flag();
                }
            } else {
                decreaseBufferBy(5);
            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketId() == PacketType.Server.KEEP_ALIVE) {
            final WrappedPacketOutKeepAlive wrapper = new WrappedPacketOutKeepAlive(event.getNMSPacket());

            keepAliveUpdates.put(wrapper.getId(), System.currentTimeMillis());
        }
    }
}
