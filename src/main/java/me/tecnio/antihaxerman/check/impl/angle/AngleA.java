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

package me.tecnio.antihaxerman.check.impl.angle;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@CheckInfo(name = "Angle", type = "A")
public final class AngleA extends Check {
    public AngleA(PlayerData data) {
        super(data);
    }

    private boolean attacked;

    @Override
    public void onFlying() {
        if (!attacked) return;
        attacked = false;

        final Entity target = data.getTarget();

        if (!(target instanceof LivingEntity)) return;
        if (data.getTargetLocations().size() < 30) return;

        final int now = AntiHaxerman.getInstance().getTickProcessor().getTicks();
        final int latencyInTicks = (int) Math.floor(data.getTransactionPing() / 50.0);

        final Vector origin = data.getPlayer().getEyeLocation().toVector().setY(0.0);

        final float angle = (float) data.getTargetLocations().stream()
                .filter(pair -> Math.abs(now - pair.getY() - latencyInTicks) < 3)
                .mapToDouble(pair -> {
                    final Vector targetLocation = pair.getX().toVector();

                    final Vector dirToDestination = targetLocation.clone().setY(0.0).subtract(origin);
                    final Vector playerDirection = data.getPlayer().getEyeLocation().getDirection().setY(0.0);

                    return dirToDestination.angle(playerDirection);
                })
                .min().orElse(-1);

        final boolean exempt = origin.distance(target.getLocation().toVector()) < 1.2;

        if (angle > 0.6 && !exempt) {
            if (increaseBuffer() > 5) {
                flag();
            }
        } else {
            resetBuffer();
        }
    }

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        attacked = true;
    }
}
