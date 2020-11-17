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

package me.tecnio.antihaxerman.manager;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.utils.data.Pair;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

public final class TickManager implements Runnable {

    @Getter private int ticks;
    private static BukkitTask task;

    public void start() {
        assert task == null : "TickManager has already been started!";

        task = Bukkit.getScheduler().runTaskTimer(AntiHaxerman.getInstance(), this, 0L, 1L);
    }

    public void stop() {
        if (task == null) return;

        task.cancel();
        task = null;
    }

    @Override
    public void run() {
        ticks++;

        PlayerDataManager.getPlayerData().values().parallelStream().forEach(data -> {
            Entity target = data.getTarget();

            if (target != null) {
                data.getTargetLocations().add(new Pair<>(target.getLocation(), ticks));
            }
        });
    }
}
