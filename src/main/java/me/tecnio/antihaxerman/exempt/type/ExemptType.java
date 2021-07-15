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

package me.tecnio.antihaxerman.exempt.type;

import lombok.Getter;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.PlayerUtil;
import me.tecnio.antihaxerman.util.ServerUtil;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;

import java.util.function.Function;

@Getter
public enum ExemptType {

    CHUNK(data -> !data.getPlayer().getWorld().isChunkLoaded(
            NumberConversions.floor(data.getPositionProcessor().getX()) >> 4,
            NumberConversions.floor(data.getPositionProcessor().getZ()) >> 4)
    ),

    TPS(data -> ServerUtil.getTPS() < 18.0D),

    TELEPORT(data -> data.getPositionProcessor().isTeleported()),

    TELEPORT_DELAY(data -> data.getPositionProcessor().getSinceTeleportTicks() < 5),

    TELEPORT_DELAY_SMALL(data -> data.getPositionProcessor().getSinceTeleportTicks() < 3),

    VELOCITY(data -> data.getVelocityProcessor().isTakingVelocity()),

    VELOCITY_ON_TICK(data -> data.getVelocityProcessor().getTicksSinceVelocity() == 1),

    SLIME(data -> data.getPositionProcessor().getSinceSlimeTicks() < 20),

    SLIME_ON_TICK(data -> data.getPositionProcessor().getSinceSlimeTicks() < 2),

    DIGGING(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastDiggingTick() < 10),

    BLOCK_BREAK(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastBreakTick() < 10),

    PLACING(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastPlaceTick() < 10),

    BOAT(data -> data.getPositionProcessor().isNearVehicle()),

    VEHICLE(data -> data.getPositionProcessor().getSinceVehicleTicks() < 20),

    LIQUID(data -> data.getPositionProcessor().getSinceLiquidTicks() < 4),

    UNDERBLOCK(data -> data.getPositionProcessor().isBlockNearHead()),

    NEAR_WALL(data -> data.getPositionProcessor().isNearWall()),

    PISTON(data -> data.getPositionProcessor().isNearPiston()),

    STAIR(data -> data.getPositionProcessor().isNearStair()),

    VOID(data -> data.getPositionProcessor().getY() < 4),

    COMBAT(data -> data.getCombatProcessor().getHitTicks() < 5),

    FLYING(data -> data.getPositionProcessor().getSinceFlyingTicks() < 40),

    AUTOCLICKER(data -> data.getExemptProcessor().isExempt(ExemptType.PLACING, ExemptType.DIGGING, ExemptType.BLOCK_BREAK)),

    WEB(data -> data.getPositionProcessor().getSinceWebTicks() < 10),

    JOINED(data -> System.currentTimeMillis() - data.getJoinTime() < 5000L),

    LAGGING(data -> {
        final long delta = data.getFlying() - data.getLastFlying();

        return delta > 100 || delta < 2;
    }),

    PING(data -> PlayerUtil.getPing(data.getPlayer()) > 500),

    CREATIVE(data -> data.getPlayer().getGameMode() == GameMode.CREATIVE),

    CINEMATIC(data -> data.getRotationProcessor().isCinematic()),

    CLIMBABLE(data -> data.getPositionProcessor().getSinceClimbableTicks() < 10),

    ICE(data -> data.getPositionProcessor().getSinceIceTicks() < 10),

    JUMP(data -> {
        final boolean onGround = data.getPositionProcessor().isOnGround();
        final boolean lastOnGround = data.getPositionProcessor().isLastOnGround();

        final double deltaY = data.getPositionProcessor().getDeltaY();
        final double lastY = data.getPositionProcessor().getLastY();

        final boolean deltaModulo = deltaY % 0.015625 == 0.0;
        final boolean lastGround = lastY % 0.015625 == 0.0;

        final boolean step = deltaModulo && lastGround;

        final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
        final double expectedJumpMotion = 0.42F + modifierJump;

        return Math.abs(expectedJumpMotion - deltaY) < 1E-5 && !onGround && lastOnGround && !step;
    }),

    GHOST_BLOCK(data -> data.getGhostBlockProcessor().isOnGhostBlock() && Config.GHOST_BLOCK_ENABLED),

    SINCE_SPEED(data -> data.getPositionProcessor().getSinceSpeedTicks() < 10 && data.getPositionProcessor().getSinceSpeedTicks() != 0),

    // SPF stands for spoofable just wanted to tell.
    CHUNK_CLIENT_SPF(data -> data.getPositionProcessor().getDeltaY() + 0.09800000190734881 <= 0.001);

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }
}
