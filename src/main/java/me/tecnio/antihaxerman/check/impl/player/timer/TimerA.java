

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
import me.tecnio.antihaxerman.util.MovingStats;

@CheckInfo(name = "Timer", type = "A", description = "Detects game speed modifications.")
public final class TimerA extends Check {

    private final MovingStats movingStats = new MovingStats(20);

    private long lastFlying = 0L;
    private long allowance = 0;

    public TimerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long now = now();

            final boolean exempt = this.isExempt(ExemptType.TPS, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.VEHICLE);

            handle: {
                if (exempt) break handle;

                final long delay = now - lastFlying;
                if (delay < 1) break handle;

                movingStats.add(delay);

                final double threshold = 7.07;
                final double deviation = movingStats.getStdDev(threshold);

                if (deviation < threshold && !Double.isNaN(deviation)) {
                    allowance += 50 - delay;

                    if (allowance > Math.ceil(threshold)) fail();
                } else {
                    allowance = 0;
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            movingStats.add(125L);
        }
    }
}
