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

package me.tecnio.antihaxerman.api;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.api.impl.AHMFlagEvent;
import me.tecnio.antihaxerman.check.Check;
import org.bukkit.Bukkit;

public final class APIManager {
    public static void callFlagEvent(final Check check) {
        final AHMFlagEvent flagEvent = new AHMFlagEvent(check.getData().getPlayer(),
                check.getCheckInfo().name(),
                check.getCheckInfo().type(),
                check.getVl(),
                check.getBuffer());

        Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getPluginManager().callEvent(flagEvent));
    }
}
