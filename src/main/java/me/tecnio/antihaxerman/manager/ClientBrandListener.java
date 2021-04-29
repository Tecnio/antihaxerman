/*
 *  Copyright (C) 2020 - 2021 Tecnio
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
