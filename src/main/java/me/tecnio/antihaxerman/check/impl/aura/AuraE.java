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

package me.tecnio.antihaxerman.check.impl.aura;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Aura", type = "E")
public final class AuraE extends Check {
    public AuraE(PlayerData data) {
        super(data);
    }

    /*
     * Credits to Elevated https://github.com/ElevatedDev/Frequency
     */

    private int movements = 0, lastMovements = 0, total = 0;

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
            final boolean proper = data.getCps() > 7.2 && movements < 4 && lastMovements < 4;

            if (proper) {
                final boolean flag = movements == lastMovements;

                if (flag) {
                    increaseBuffer();
                }

                if (++total == 30) {

                    if (buffer > 28)
                        flag();

                    total = 0;
                }
            }

            lastMovements = movements;
            movements = 0;
        }
    }

    @Override
    public void onFlying() {
        ++movements;
    }
}
