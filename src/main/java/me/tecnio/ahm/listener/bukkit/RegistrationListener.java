package me.tecnio.ahm.listener.bukkit;

import me.tecnio.ahm.AHM;
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
        AHM.get(PlayerDataManager.class).add(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeave(final PlayerQuitEvent event) {
        final PlayerData data = AHM.get(PlayerDataManager.class).getPlayerData(event.getPlayer().getUniqueId());

        if (data == null) return;

        data.terminate();

        AHM.get(PlayerDataManager.class).remove(event.getPlayer());
    }
}
