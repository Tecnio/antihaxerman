package me.tecnio.antihaxerman.listeners;

import me.tecnio.antihaxerman.Config;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        DataManager.INSTANCE.register(new PlayerData(event.getPlayer().getUniqueId()));
        if (Config.ENABLE_ALERTS_ON_JOIN && event.getPlayer().hasPermission("antihaxerman.alerts"))DataManager.INSTANCE.getUser(event.getPlayer().getUniqueId()).setAlerts(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        DataManager.INSTANCE.unregister(DataManager.INSTANCE.getUser(event.getPlayer().getUniqueId()));
    }
}
