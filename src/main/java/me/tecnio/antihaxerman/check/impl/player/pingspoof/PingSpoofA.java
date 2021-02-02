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

package me.tecnio.antihaxerman.check.impl.player.pingspoof;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "PingSpoof", type = "A", description = "Checks the delta of the keep alive delay and transaction delay.")
public final class PingSpoofA extends Check {
    public PingSpoofA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long transactionDelay = data.getConnectionProcessor().getTransactionPing();
            final long keepAliveDelay = data.getConnectionProcessor().getKeepAlivePing();

            final long delta = Math.abs(keepAliveDelay - transactionDelay);

            final boolean exempt = isExempt(ExemptType.JOINED, ExemptType.TELEPORT, ExemptType.LAGGING);
            final boolean invalid = delta > 50;

            if (invalid && !exempt) {
                if (increaseBuffer() > 200) {
                    fail();
                }
            } else {
                decreaseBufferBy(5);
            }
        }
    }
}
