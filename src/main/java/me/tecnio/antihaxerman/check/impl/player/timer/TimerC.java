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
import me.tecnio.antihaxerman.util.MathUtil;
import me.tecnio.antihaxerman.util.type.EvictingList;

@CheckInfo(name = "Timer", type = "C", description = "Checks for game speed changes.")
public final class TimerC extends Check {

    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlying;

    public TimerC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.TPS)) {
            final long now = now();
            final long delta = now - lastFlying;

            if (delta > 0) {
                samples.add(delta);
            }

            if (samples.isFull()) {
                final double average = MathUtil.getAverage(samples);
                final double speed = 50 / average;

                if (speed >= 1.025) {
                    if (increaseBuffer() > 20) {
                        fail();
                    }
                } else {
                    decreaseBuffer();
                }
            }

            lastFlying = now;
        } else if (packet.isTeleport()) {
            samples.add(135L);
        }
    }
}
