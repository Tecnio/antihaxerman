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

package me.tecnio.antihaxerman.data.processor;

import lombok.Getter;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.MathUtil;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * This is a ghost block processor made to lag back players who go on a ghost block.
 * Its not really a great processor but better than nothing.
 */
@Getter
public final class GhostBlockProcessor {

    private final PlayerData data;

    private boolean onGhostBlock;
    private int ghostTicks;

    private Location lastGroundLocation;

    public GhostBlockProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handleFlying() {
        if (!Config.GHOST_BLOCK_ENABLED) return;

        onGhostBlock = data.getPositionProcessor().isOnGround()
                && data.getPositionProcessor().getY() % 0.015625 < 0.03
                && data.getPositionProcessor().isInAir()
                && data.getPositionProcessor().getAirTicks() > 2;

        if (onGhostBlock) ++ghostTicks;
        else ghostTicks = 0;

        if (Config.GHOST_BLOCK_LAG_BACK) {
            int ticks = 0;

            final int ping = MathUtil.msToTicks(PlayerUtil.getPing(data.getPlayer()));

            switch (Config.GHOST_BLOCK_MODE) {
                case STRICT:
                    ticks = 0;
                    break;
                case LENIENT:
                    ticks = ping;
                    break;
                case NORMAL:
                    ticks = Math.min(ping, Math.round(Config.GHOST_BLOCK_MAX_PING / 50F));
                    break;
            }

            if (ghostTicks > ticks && lastGroundLocation != null) {
                Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () ->
                        data.getPlayer().teleport(lastGroundLocation, PlayerTeleportEvent.TeleportCause.PLUGIN));
            }
        }

        if (!data.getPositionProcessor().isInAir()
                && data.getPositionProcessor().isOnGround()) {
            final Location location = data.getPositionProcessor().getLocation().clone();

            location.setYaw(data.getRotationProcessor().getYaw());
            location.setPitch(data.getRotationProcessor().getPitch());

            lastGroundLocation = location;
        }
    }

    public enum Mode {
        NORMAL, STRICT, LENIENT
    }
}
