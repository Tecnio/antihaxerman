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

package me.tecnio.antihaxerman.exempt.type;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.ServerUtil;
import lombok.Getter;
import org.bukkit.GameMode;

import java.util.function.Function;

@Getter
public enum ExemptType {

    CHUNK(data -> !data.getPlayer().getWorld().isChunkLoaded((int) Math.floor(data.getPositionProcessor().getX()) << 4, (int) Math.floor(data.getPositionProcessor().getZ()) << 4)),

    TPS(data -> ServerUtil.getTPS() < 18.5D),

    TELEPORT(data -> data.getPositionProcessor().getTeleportTicks() < 40),

    VELOCITY(data -> data.getVelocityProcessor().isTakingVelocity()),

    SLIME(data -> data.getPositionProcessor().getSinceSlimeTicks() < 20),

    DIGGING(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastDiggingTick() < 10),

    BLOCK_BREAK(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastBreakTick() < 10),

    PLACING(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastPlaceTick() < 10),

    BOAT(data -> data.getPositionProcessor().isNearBoat()),

    VEHICLE(data -> data.getPositionProcessor().getSinceVehicleTicks() < 20),

    LIQUID(data -> data.getPositionProcessor().getSinceLiquidTicks() < 20),

    UNDERBLOCK(data -> data.getPositionProcessor().isBlockNearHead()),

    PISTON(data -> data.getPositionProcessor().isNearPiston()),

    VOID(data -> data.getPlayer().getLocation().getY() < 4),

    COMBAT(data -> data.getCombatProcessor().getHitTicks() < 5),

    FLYING(data -> data.getPositionProcessor().getSinceFlyingTicks() < 40),

    AUTOCLICKER(data -> data.getExemptProcessor().isExempt(ExemptType.PLACING, ExemptType.DIGGING, ExemptType.BLOCK_BREAK)),

    WEB(data -> data.getPositionProcessor().getSinceWebTicks() < 10),

    JOINED(data -> System.currentTimeMillis() - data.getJoinTime() < 5000L),

    LAGGING(data -> data.getFlying() - data.getLastFlying() < 5),

    KEEPALIVE(data -> !data.getConnectionProcessor().getKeepAliveTime(AntiHaxerman.INSTANCE.getTickManager().getTicks()).isPresent()),

    CREATIVE(data -> data.getPlayer().getGameMode() == GameMode.CREATIVE),

    CLIMBABLE(data -> data.getPositionProcessor().getSinceClimbableTicks() < 10);

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }
}
