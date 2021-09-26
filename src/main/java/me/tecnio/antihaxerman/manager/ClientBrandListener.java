

package me.tecnio.antihaxerman.manager;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;

public final class ClientBrandListener implements PluginMessageListener, Listener {

    @Override
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] msg) {
        try {
            final PlayerData data = PlayerDataManager.getInstance().getPlayerData(player);

            if (data == null) return;
            if (msg.length == 0) return;

            final String clientBrand = new String(msg, StandardCharsets.UTF_8).length() > 0 ? new String(msg, StandardCharsets.UTF_8).substring(1) : new String(msg, StandardCharsets.UTF_8);

            data.setClientBrand(clientBrand);

            handle: {
                if (!Config.CLIENT_ENABLED) break handle;

                if (Config.CLIENT_CASE_SENSITIVE) {
                    if (Config.BLOCKED_CLIENTS.stream().noneMatch(clientBrand::contains)) {
                        break handle;
                    }
                } else {
                    if (Config.BLOCKED_CLIENTS
                            .stream().noneMatch(s -> clientBrand.toLowerCase().contains(s.toLowerCase()))) {
                        break handle;
                    }
                }

                Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> player.kickPlayer(Config.CLIENT_KICK_MESSAGE));
            }
        } catch (final Throwable t) {
            System.out.println("An error occurred with ClientBrandListener. You can ignore this.");
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        addChannel(event.getPlayer());
    }

    private void addChannel(final Player player) {
        try {
            player.getClass().getMethod("addChannel", String.class).invoke(player, "MC|BRAND");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
