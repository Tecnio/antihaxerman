package me.tecnio.ahm.listener.bukkit;

import me.tecnio.ahm.AntiHaxerman;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class RegistrationListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        AntiHaxerman.get(PlayerDataManager.class).add(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(final PlayerQuitEvent event) {
        final PlayerDataManager dataManager = AntiHaxerman.get(PlayerDataManager.class);
        final PlayerData data = dataManager.get(event.getPlayer().getUniqueId());

        if (data == null) return;

        data.terminate();
        dataManager.remove(event.getPlayer());
    }
}
