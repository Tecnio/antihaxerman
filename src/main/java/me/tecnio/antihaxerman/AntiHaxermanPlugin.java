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

package me.tecnio.antihaxerman;

import org.bukkit.plugin.java.JavaPlugin;

public final class AntiHaxermanPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        AntiHaxerman.INSTANCE.load(this);
    }

    @Override
    public void onEnable() {
        AntiHaxerman.INSTANCE.start(this);
    }

    @Override
    public void onDisable() {
        AntiHaxerman.INSTANCE.stop(this);
    }

}
