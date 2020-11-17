/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

package me.tecnio.antihaxerman.check.impl.timer;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.math.MovingStats;

@CheckInfo(name = "Timer", type = "A")
public final class TimerA extends Check {
    public TimerA(PlayerData data) {
        super(data);
    }

    /*
     * Credits to Elevated https://github.com/ElevatedDev/Frequency
     */

    private long lastFlying = 0L;
    private final MovingStats movingStats = new MovingStats(20);

    @Override
    public void onFlying() {
        final long now = System.currentTimeMillis();

        final boolean exempt = data.teleportTicks() < 20 || PacketEvents.getAPI().getServerUtils().getTPS() < 18 || now - lastFlying < 1;

        sample: {
            if (exempt) break sample;

            movingStats.add(now - lastFlying);
        }

        analyze: {
            final double threshold = 7.07;
            final double deviation = movingStats.getStdDev(threshold);

            if (deviation >= threshold || Double.isNaN(deviation)) {
                resetBuffer();

                break analyze;
            }

            if (increaseBuffer() > 30) {
                flag();
            }
        }

        this.lastFlying = now;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.STEER_VEHICLE) {
            if (data.getPlayer().isInsideVehicle()) {
                resetBuffer();
            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketId() == PacketType.Server.POSITION) {
            resetBuffer();
        }
    }
}
