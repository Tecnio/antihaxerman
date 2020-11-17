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

package me.tecnio.antihaxerman.check.impl.autoclicker;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.math.MathUtils;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

import java.util.Deque;
import java.util.LinkedList;

@CheckInfo(name = "AutoClicker", type = "C")
public final class AutoClickerC extends Check {
    public AutoClickerC(PlayerData data) {
        super(data);
    }

    private final Deque<Double> delays = new LinkedList<>();
    private int ticks;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.ARM_ANIMATION) {
            if (!data.isDigging()) {
                if (ticks < 10) {
                    delays.add((double) ticks);

                    if (delays.size() >= 120) {
                        final double kurtosis = MathUtils.getKurtosis(delays);

                        if (Double.isNaN(kurtosis)) {
                            if (increaseBuffer() > 1) {
                                flag();
                            }
                        } else resetBuffer();

                        delays.clear();
                    }
                }
                ticks = 0;
            } else {
                resetBuffer();
            }
        } else if (PacketUtils.isFlyingPacket(event.getPacketId())) {
            ticks++;
        }
    }
}
