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

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "BadPackets", type = "F", description = "Checks if player is sending flyings but not responding transactions.")
public final class BadPacketsF extends Check {

    public BadPacketsF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long lastSent = data.getConnectionProcessor().getLastTransactionSent();
            final long lastReceived = data.getConnectionProcessor().getLastTransactionReceived();

            final long difference = Math.abs(lastReceived - lastSent);

            final boolean exempt = isExempt(ExemptType.TPS, ExemptType.JOINED, ExemptType.TELEPORT);
            final boolean invalid = difference > 1000;

            if (invalid && !exempt) {
                kick("Internal Exception: java.io.IOException: An existing connection was forcibly closed by the remote host");
            }
        }
    }
}
