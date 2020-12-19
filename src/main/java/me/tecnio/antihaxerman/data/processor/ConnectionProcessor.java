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

package me.tecnio.antihaxerman.data.processor;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.type.EvictingMap;

import java.util.Random;

@Getter
public final class ConnectionProcessor {

    private final PlayerData data;

    private final EvictingMap<Short, Long> transactionUpdates = new EvictingMap<>(20);
    private final EvictingMap<Long, Long> keepAliveUpdates = new EvictingMap<>(20);

    private short transactionId;
    private long keepAliveId;

    private long keepAlivePing;
    private long transactionPing;

    public ConnectionProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleIncomingTransaction(final WrappedPacketInTransaction wrapper) {
        final long now = System.currentTimeMillis();

        transactionUpdates.computeIfPresent(wrapper.getActionNumber(), (id, time) -> {
            transactionPing = now - time;
            transactionUpdates.remove(id);

            return time;
        });
    }

    public void handleIncomingKeepAlive(final WrappedPacketInKeepAlive wrapper) {
        final long now = System.currentTimeMillis();

        keepAliveUpdates.computeIfPresent(wrapper.getId(), (id, time) -> {
            keepAlivePing = now - time;
            keepAliveUpdates.remove(id);

            return time;
        });
    }

    public void handleFlying() {
        final Random random = new Random();

        short transactionId = (short) (random.nextInt(32767));
        transactionId = transactionId == data.getVelocityProcessor().getVelocityID() ? (short) (transactionId - 1) : transactionId;

        final int keepAliveId = random.nextInt();

        final WrappedPacketOutTransaction transaction = new WrappedPacketOutTransaction(0, transactionId, false);
        //final WrappedPacketOutKeepAlive keepAlive = new WrappedPacketOutKeepAlive(keepAliveId);

        PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), transaction);
        //PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), keepAlive);
    }

    public void handleOutgoingTransaction(final WrappedPacketOutTransaction wrapper) {
        final short actionNumber = wrapper.getActionNumber();

        transactionId = actionNumber;
        transactionUpdates.put(actionNumber, System.currentTimeMillis());
    }

    public void handleOutgoingKeepAlive(final WrappedPacketOutKeepAlive wrapper) {
        final long id = wrapper.getId();

        keepAliveId = id;
        keepAliveUpdates.put(id, System.currentTimeMillis());
    }
}
