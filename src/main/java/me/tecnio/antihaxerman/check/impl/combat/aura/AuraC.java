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

package me.tecnio.antihaxerman.check.impl.combat.aura;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.entity.Entity;

@CheckInfo(name = "Aura", type = "C", description = "Checks for switch aura.")
public final class AuraC extends Check {

    private int ticks;

    public AuraC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();

            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean exempt = target == lastTarget;
            final boolean invalid = (deltaYaw == 0.0 && ticks <= 5) || (deltaYaw > 5 && ticks < 2);

            handle: {
                if (exempt) break handle;

                if (invalid) {
                    if (increaseBuffer() > 1) {
                        fail();
                    }
                } else {
                    resetBuffer();
                }
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ticks++;
        }
    }
}
