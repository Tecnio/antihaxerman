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
import org.bukkit.entity.Entity;

@CheckInfo(name = "Aura", type = "C")
public final class AuraC extends Check {
    public AuraC(PlayerData data) {
        super(data);
    }

    private int ticks;
    private Entity lastTarget;

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        final Entity target = wrapper.getEntity();

        if (target != lastTarget) {
            if ((data.getDeltaYaw() == 0.0 && ticks <= 5) || (data.getDeltaYaw() > 5 && ticks < 2)) {
                if (increaseBuffer() > 1) {
                    flag();
                }
            } else {
                resetBuffer();
            }
        }

        ticks = 0;
        lastTarget = target;
    }

    @Override
    public void onFlying() {
        ticks++;
    }
}
