package me.tecnio.antihaxerman.listener.bukkit;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.manager.AFKManager;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.util.PlayerUtil;
import me.tecnio.antihaxerman.util.type.VpnInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public final class RegistrationListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) throws IOException {
        PlayerDataManager.getInstance().add(event.getPlayer());
        if (AntiHaxerman.INSTANCE.getUpdateChecker().isUpdateAvailable()) {
            if (event.getPlayer().hasPermission("ahm.alerts")) {
                final String version = AntiHaxerman.INSTANCE.getVersion();
                final String latestVersion = AntiHaxerman.INSTANCE.getUpdateChecker().getLatestVersion();

                AlertManager.sendMessage("An update is available for &cAntiHaxerman&8! You have &c" + version + "&8 latest is &c" + latestVersion + "&8.");
            }
        }
        if(Config.VPN_ENABLED) {
            VpnInfo info = PlayerUtil.isUsingVPN(event.getPlayer());
            if(!info.getIsVpn()) {
                return;
            }
            event.getPlayer().kickPlayer(Config.VPN_MESSAGE.replaceAll("%country%", info.getCountry()));
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("ahm.alerts")) {
                    p.sendMessage(Config.PREFIX + event.getPlayer().getName() + " Tried to join With VPN/Proxy. Country: " + info.getCountry());
                }
            }
        }
    }
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        PlayerDataManager.getInstance().remove(event.getPlayer());
        PlayerDataManager.getInstance().suspectedPlayers.remove(event.getPlayer());
        BukkitEventManager.wannadelet.remove(event.getPlayer());
        AFKManager.INSTANCE.removePlayer(event.getPlayer());
    }
}
