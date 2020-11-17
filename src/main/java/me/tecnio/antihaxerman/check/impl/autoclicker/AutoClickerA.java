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
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

@CheckInfo(name = "AutoClicker", type = "A")
public final class AutoClickerA extends Check {
    public AutoClickerA(PlayerData data) {
        super(data);
    }

    private int flyingTicks;
    private double clicksPerSecond;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.ARM_ANIMATION) {
            if (!data.isDigging() && flyingTicks <= 10) {
                final double speed = 1000 / ((flyingTicks * 50.0) > 0 ? (flyingTicks * 50.0) : 50);

                clicksPerSecond = ((clicksPerSecond * 19) + speed) / 20;

                if (clicksPerSecond >= 25) {
                    flag("cps = " + clicksPerSecond);
                }
            }

            flyingTicks = 0;
        } else if (PacketUtils.isFlyingPacket(event.getPacketId())) {
            flyingTicks++;
        }
    }
}
