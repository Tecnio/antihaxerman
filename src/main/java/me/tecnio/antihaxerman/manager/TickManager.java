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
