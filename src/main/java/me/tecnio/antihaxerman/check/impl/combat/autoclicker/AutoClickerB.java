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

package me.tecnio.antihaxerman.check.impl.combat.autoclicker;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;
import me.tecnio.antihaxerman.util.type.EvictingList;

@CheckInfo(name = "AutoClicker", type = "B", description = "Checks for consistent click pattern.")
public final class AutoClickerB extends Check {

    private final EvictingList<Long> tickList = new EvictingList<>(30);
    private double lastDeviation;
    private int tick;

    public AutoClickerB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation()) {
            final boolean exempt = isExempt(ExemptType.AUTOCLICKER);
            if (!exempt) tickList.add((long) (tick * 50.0));

            if (tickList.isFull()) {
                final double deviation = MathUtil.getStandardDeviation(tickList);
                final double difference = Math.abs(deviation - lastDeviation);

                final boolean invalid = difference < 6;

                if (invalid && !exempt) {
                    if (increaseBuffer() > 5) {
                        fail("deviation=" + deviation + " difference=" + difference);
                    }
                } else {
                    decreaseBuffer();
                }

                lastDeviation = deviation;
            }
        } else if (packet.isFlying()) {
            tick++;
        }
    }
}
