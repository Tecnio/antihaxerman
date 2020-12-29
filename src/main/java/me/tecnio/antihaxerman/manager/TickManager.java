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

package me.tecnio.antihaxerman.manager;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.type.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public final class TickManager implements Runnable {

    @Getter
    private int ticks;
    private static BukkitTask task;

    public void start() {
        assert task == null : "TickProcessor has already been started!";

        task = Bukkit.getScheduler().runTaskTimer(AntiHaxerman.INSTANCE.getPlugin(), this, 0L, 1L);
    }

    public void stop() {
        if (task == null) return;

        task.cancel();
        task = null;
    }

    @Override
    public void run() {
        ++ticks;

        for (final PlayerData data : PlayerDataManager.getInstance().getAllData()) {
            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();

            if (target != null && lastTarget != null) {
                if (target != lastTarget) {
                    data.getTargetLocations().clear();
                }
                final Location location = target.getLocation();
                data.getTargetLocations().add(new Pair<>(location, ticks));
            }

            final Random random = new Random();

            transaction: {
                if (ticks == 1) break transaction;

                short transactionId = (short) (random.nextInt(32767));
                transactionId = transactionId == data.getVelocityProcessor().getVelocityID() ? (short) (transactionId - 1) : transactionId;

                final WrappedPacketOutTransaction transaction = new WrappedPacketOutTransaction(0, transactionId, false);

                PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), transaction);
            }

            keepalive: {
                if (ticks == 1) break keepalive;

                final int keepAliveId = ticks;

                final WrappedPacketOutKeepAlive keepAlive = new WrappedPacketOutKeepAlive(keepAliveId);

                PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), keepAlive);
            }
        }
    }
}
