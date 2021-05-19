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

package me.tecnio.antihaxerman.check.impl.player.timer;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Timer", type = "D", description = "Uses a balance/allowance system to flag game speed changes.")
public final class TimerD extends Check {

    private long balance = 0L;
    private long lastFlying = 0L;

    public TimerD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long now = packet.getTimeStamp();

            handle: {
                if (isExempt(ExemptType.TPS, ExemptType.JOINED)) break handle;
                if (lastFlying == 0L) break handle;

                final long delay = now - lastFlying;

                balance += 50L;
                balance -= delay;

                if (balance > 0L) {
                    if (increaseBuffer() > 5) {
                        fail("balance: " + balance);
                    }

                    balance = 0;
                } else {
                    decreaseBufferBy(0.001);
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            if (isExempt(ExemptType.TPS, ExemptType.JOINED)) return;
            if (lastFlying == 0L) return;

            balance -= 50L;
        }
    }
}
