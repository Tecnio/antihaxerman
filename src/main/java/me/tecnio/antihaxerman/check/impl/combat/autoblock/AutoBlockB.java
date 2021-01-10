/*
 *  Copyright (C) 2020 Tecnio
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

package me.tecnio.antihaxerman.check.impl.combat.autoblock;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "AutoBlock", type = "B", description = "Checks if player is blocking in a unlikely manner.")
public class AutoBlockB extends Check {

    private boolean attacked;
    private int ticks;

    public AutoBlockB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            attacked = true;
        } else if (packet.isBlockPlace()) {
            final double cps = data.getClickProcessor().getCps();

            if (attacked) {
                if (ticks < 2 && cps > 5.0) {
                    if (increaseBuffer() > 4) {
                        fail();
                    }
                } else {
                    resetBuffer();
                }
                attacked = false;
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ++ticks;
        }
    }
}
