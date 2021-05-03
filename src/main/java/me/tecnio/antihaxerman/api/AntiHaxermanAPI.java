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

package me.tecnio.antihaxerman.api;

import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import org.bukkit.entity.Player;

public final class AntiHaxermanAPI {
    /**
     * Gets the PlayerData for the player given as a paramter.
     * Be careful as it has very important stuff inside.
     *
     * @param player The player wanted.
     * @return The PlayerData instance.
     */
    public static PlayerData getPlayerData(final Player player) {
        return PlayerDataManager.getInstance().getPlayerData(player);
    }
}
