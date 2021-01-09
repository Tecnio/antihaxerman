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

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

@CheckInfo(name = "BadPackets", type = "N", description = "Checks for disablers.")
public final class BadPacketsN extends Check implements Listener {
    public BadPacketsN(final PlayerData data) {
        super(data);
        AntiHaxerman.INSTANCE.getPlugin().getServer().getPluginManager().registerEvents(this, AntiHaxerman.INSTANCE.getPlugin());
    }

    @Override
    public void handle(final Packet packet) {
    }

    @EventHandler
    public void handleTeleport(final PlayerTeleportEvent event) {
        if (event.getPlayer() == data.getPlayer()) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                final double deltaXZ = Math.abs(data.getPositionProcessor().getDeltaXZ());
                final double lastDeltaXZ = Math.abs(data.getPositionProcessor().getLastDeltaXZ());

                final double deltaY = Math.abs(data.getPositionProcessor().getDeltaY());
                final double lastDeltaY = Math.abs(data.getPositionProcessor().getLastDeltaY());

                final boolean invalid = deltaXZ > 10.0 || lastDeltaXZ > 10.0 || deltaY > 10.0 || lastDeltaY > 10.0;

                if (invalid) fail();

                debug(String.format("dXZ: %.2f ldXZ: %.2f dY: %.2f ldY: %.2f", deltaXZ, lastDeltaXZ, deltaY, lastDeltaY));
            }
        }
    }
}
