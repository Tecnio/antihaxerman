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

package me.tecnio.antihaxerman.check.impl.reach;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach", type = "A", autoBan = false)
public final class ReachA extends Check {
    public ReachA(PlayerData data) {
        super(data);
    }

    private boolean attacked;

    @Override
    public void onFlying() {
        if (!attacked) return;
        attacked = false;

        final Entity target = data.getTarget();

        if (!(target instanceof Player)) return;
        if (data.getTargetLocations().size() < 30) return;

        final int now = AntiHaxerman.getInstance().getTickProcessor().getTicks();
        final int latencyInTicks = (int) Math.floor(data.getTransactionPing() / 50.0);

        final Vector origin = data.getLocation().toVector().setY(0.0);

        final double maxDistance = data.getPlayer().getGameMode() == GameMode.CREATIVE ? 6.0 : 3.0;
        final double distance = data.getTargetLocations().stream()
                .filter(pair -> Math.abs(now - pair.getY() - latencyInTicks) < 2)
                .mapToDouble(pair -> {
                    final Vector targetLocation = pair.getX().toVector().setY(0.0);

                    return origin.distance(targetLocation) - 0.565686;
                })
                .min().orElse(-1);

        if (distance > maxDistance) {
            if (increaseBuffer() > 4) {
                flag("distance: " + distance);
            }
        } else {
            if (data.isLagging()) {
                decreaseBufferBy(0.1);
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        attacked = true;
    }
}
