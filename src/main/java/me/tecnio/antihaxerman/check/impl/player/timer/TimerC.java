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

@CheckInfo(name = "Timer", type = "C", description = "Checks for game speed changes via sampling & averaging.")
public final class TimerC extends Check {
    private final EvictingList<Long> largesamples = new EvictingList<>(620);
    //31 seconds worth of flyings.
    private final EvictingList<Long> samples = new EvictingList<>(50);
    //2.5 seconds worth of flyings.
    private long lastFlying;

    public TimerC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.TPS, ExemptType.JOINED) {
            //Don't exempt for exempt type 'lagging' on a timer check.
            final long now = now();
            final long delta = now - lastFlying;

            samples.add(delta);
            largesamples.add(delta);
            

            if (samples.isFull()) {
                final double longspeed = 50 / MathUtil.getAverage(largesamples);
                final double speed = 50 /  MathUtil.getAverage(samples);

                if (speed >= 1.025 && longspeed > 1.025 && delta < 50) {
                    if (increaseBuffer() > 4) {
                        fail();
                        setBuffer(3.5);
                    }
                } else {
                    decreaseBufferBy(2.5);
                }
            }

            lastFlying = now;
        } else if (packet.isTeleport()) {
            samples.add(135L);
            largesamples.add(135L);
        }
    }
}
