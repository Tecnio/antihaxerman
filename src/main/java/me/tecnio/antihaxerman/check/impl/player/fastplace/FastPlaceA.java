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

package me.tecnio.antihaxerman.check.impl.player.fastplace;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "FastPlace", type = "A", description = "Checks if player placing blocks too fast.")
public final class FastPlaceA extends Check implements Listener {

    private int blocks, movements;

    public FastPlaceA(final PlayerData data) {
        super(data);
        AntiHaxerman.INSTANCE.getPlugin().getServer().getPluginManager().registerEvents(this, AntiHaxerman.INSTANCE.getPlugin());
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            ++movements;

            if (movements >= 20) {
                if (blocks > 18) {
                    fail();
                }

                movements = 0;
                blocks = 0;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.getPlayer() == data.getPlayer()) {
            ++blocks;
        }
    }
}
