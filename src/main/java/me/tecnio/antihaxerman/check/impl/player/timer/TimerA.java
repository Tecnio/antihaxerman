

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
import me.tecnio.antihaxerman.util.type.EvictingList;
import me.tecnio.antihaxerman.util.MathUtil;

@CheckInfo(name = "Timer", type = "A", description = "Detects slow timer via sampling and averaging.")
public final class TimerA extends Check {

    private final EvictingList<Long> largesamples = new EvictingList<>(100);
    //five seconds worth of flyings.
    private final EvictingList<Long> samples = new EvictingList<>(20);
    //one second worth of flyings.

    private long lastFlying = 0L;

    public TimerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long now = now();
            final long delta = now - lastFlying;
            if(lastFlying != 0 && !this.isExempt(ExemptType.TPS, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.VEHICLE)) {
                //Make sure delta is correct make sure not exempt and conditions to sample are good
                samples.add(delta);
                largesamples.add(delta);
                if(largesamples.isFull()) {
                    //make sure samples are full
                    double speed = 50 / MathUtil.getAverage(samples);
                    double reliablespeed = 50 / MathUtil.getAverage(largesamples);
                    //Calculates two different speeds with different sample sizes
                    double drift = Math.abs(speed - reliablespeed);
                    //Calculates change of speed over 5 seconds.
                    
                    double std = MathUtil.getStandardDeviation(reliablespeed);
                    double std2 = MathUtil.getStandardDeviation(speed);
                    //Checks how accurate both speed estimates are.
                    
                    if(reliablespeed <= 0.95 && speed <= 0.95 && (std <= 64.0 && std2 <= 42.0 && drift <= 0.01 || std <= 350.0 && std2 <= 64.0 && drift <= 0.001)) {
                        //If we have somewhat high confidence of both speeds, and the drift is low and both speeds are < 95%, flag.
                        if(increaseBuffer() > 3) {
                            //Just incase this condition is met for some reason by a legit. (which should be rare).
                            fail("Drift: " + drift + " Standard Dev: " + std + " STD2: " + std2 + " Speed: " + reliablespeed);
                            //Some debug info
                            setBuffer(2.5);
                        }
                    } else {
                        this.decreaseBufferBy(0.025);
                    }
                }
            }
            lastFlying = now;
        }
    }
}
