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

package me.tecnio.antihaxerman.data.processor;

import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.type.EvictingMap;

import java.util.Map;
import java.util.Optional;

@Getter
public final class ConnectionProcessor {

    private final PlayerData data;

    private final EvictingMap<Short, Long> transactionUpdates = new EvictingMap<>(20);
    private final EvictingMap<Long, Long> keepAliveUpdates = new EvictingMap<>(20);

    private short transactionId;
    private long keepAliveId;

    private long keepAlivePing;
    private long transactionPing;

    private long lastTransactionSent;
    private long lastTransactionReceived;

    private long lastKeepAliveSent;
    private long lastKeepAliveReceived;

    public ConnectionProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleIncomingTransaction(final WrappedPacketInTransaction wrapper) {
        final long now = System.currentTimeMillis();

        transactionUpdates.computeIfPresent(wrapper.getActionNumber(), (id, time) -> {
            transactionPing = now - time;
            lastTransactionReceived = now;

            return time;
        });
    }

    public void handleIncomingKeepAlive(final WrappedPacketInKeepAlive wrapper) {
        final long now = System.currentTimeMillis();

        keepAliveUpdates.computeIfPresent(wrapper.getId(), (id, time) -> {
            keepAlivePing = now - time;
            lastKeepAliveReceived = now;

            return time;
        });
    }

    public void handleOutgoingTransaction(final WrappedPacketOutTransaction wrapper) {
        final long now = System.currentTimeMillis();
        final short actionNumber = wrapper.getActionNumber();

        lastTransactionSent = now;

        transactionId = actionNumber;
        transactionUpdates.put(actionNumber, System.currentTimeMillis());
    }

    public void handleOutgoingKeepAlive(final WrappedPacketOutKeepAlive wrapper) {
        final long now = System.currentTimeMillis();
        final long id = wrapper.getId();

        lastKeepAliveSent = now;

        keepAliveId = id;
        keepAliveUpdates.put(id, System.currentTimeMillis());
    }

    public Optional<Long> getTransactionTime(final short actionNumber) {
        final Map<Short, Long> entries = transactionUpdates;

        if (entries.containsKey(actionNumber)) return Optional.of(entries.get(actionNumber));

        return Optional.empty();
    }

    public Optional<Long> getKeepAliveTime(final long identification) {
        final Map<Long, Long> entries = keepAliveUpdates;

        if (entries.containsKey(identification)) return Optional.of(entries.get(identification));

        return Optional.empty();
    }
}
