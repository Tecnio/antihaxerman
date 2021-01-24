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

package me.tecnio.antihaxerman.listener.bukkit;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.util.ColorUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class RegistrationListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        PlayerDataManager.getInstance().add(event.getPlayer());

        if (AntiHaxerman.INSTANCE.isUpdateAvailable()) {
            if (event.getPlayer().isOp()) {
                final String version = AntiHaxerman.INSTANCE.getVersion();
                final String latestVersion = AntiHaxerman.INSTANCE.getUpdateChecker().getLatestVersion();

                AlertManager.sendMessage("An update is available for &cAntiHaxerman&8! You have &c" + version + "&8 latest is &c" + latestVersion + "&8.");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        PlayerDataManager.getInstance().remove(event.getPlayer());
    }
}
