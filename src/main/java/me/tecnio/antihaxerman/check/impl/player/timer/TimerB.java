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

@CheckInfo(name = "Timer", type = "B", description = "Checks packet delay between packets.", experimental = true)
public final class TimerB extends Check {

    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlying;

    public TimerB(final PlayerData data) {
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

                if (delay > 0) {
                    samples.add(delay);
                }

                if (samples.isFull()) {
                    final double average = MathUtil.getAverage(samples);
                    final double deviation = MathUtil.getStandardDeviation(samples);

                    final double speed = 50.0 / average;

                    final boolean invalid = deviation < 40.0 && speed < 0.6 && !Double.isNaN(deviation);

                    if (invalid) {
                        if (increaseBuffer() > 30) {
                            fail();
                            multiplyBuffer(0.50);
                        }
                    } else {
                        decreaseBufferBy(10);
                    }
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            samples.add(125L);
        }
    }
}
