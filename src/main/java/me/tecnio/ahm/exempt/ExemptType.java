package me.tecnio.ahm.exempt;

import lombok.Getter;
import me.tecnio.ahm.data.PlayerData;
import org.bukkit.util.NumberConversions;

import java.util.function.Function;

@Getter
public enum ExemptType {

    CHUNK(data -> !data.getPlayer().getWorld().isChunkLoaded(
            NumberConversions.floor(data.getPositionTracker().getX()) >> 4,
            NumberConversions.floor(data.getPositionTracker().getZ()) >> 4
    )),

    JOIN(data -> data.getTicks() < 150),

    VEHICLE(data -> data.getPlayer().isInsideVehicle()),

    TELEPORT(data -> data.getPositionTracker().isTeleported()),

    TELEPORTED_RECENTLY(data -> data.getPositionTracker().getTicksSinceTeleport().hasNotPassed(2)),

    VELOCITY(data -> data.getVelocityTracker().getTicksSinceVelocity() == 1),

    RETARD(data -> !data.getPositionTracker().isLastPosition() || !data.getPositionTracker().isLastLastPosition()),

    LIQUID(data -> data.getPositionTracker().isWater()
            || data.getPositionTracker().isLastWater()
            || data.getPositionTracker().isLava()
            || data.getPositionTracker().isLastLava()),

    WEB(data -> data.getPositionTracker().isWeb() || data.getPositionTracker().isLastWeb()),

    CLIMBABLE(data -> data.getPositionTracker().isClimbable() || data.getPositionTracker().isLastClimbable()),

    UNDER_BLOCK(data -> data.getPositionTracker().isUnderBlock() || data.getPositionTracker().isLastUnderBlock()),

    PISTON(data -> data.getPositionTracker().isPiston() || data.getPositionTracker().isLastPiston()),

    BOAT(data -> data.getPositionTracker().isBoat()),

    SLIME(data -> data.getPositionTracker().isSlime() || data.getPositionTracker().isLastSlime()),

    SOUL_SAND(data -> data.getPositionTracker().isSoulSand() || data.getPositionTracker().isLastSoulSand()),

    ICE(data -> data.getPositionTracker().isIce() || data.getPositionTracker().isLastIce()),

    WALL(data -> data.getPositionTracker().isWall() || data.getPositionTracker().isLastWall()),

    STEP(data -> data.getPositionTracker().isFucked() || data.getPositionTracker().isLastFucked()),

    AIM(data -> data.getActionTracker().getAttackTimer().hasPassed(3)
            || data.getPositionTracker().getTicksSinceTeleport().hasNotPassed(2)),

    EXPLOSION(data -> data.getVelocityTracker().getTicksSinceExplosion() < 2),

    FLIGHT(data -> data.getPlayer().getAllowFlight() || data.getPlayer().isFlying()),

    SNEAK_EDGE(data -> data.getActionTracker().isSneaking() && data.getPositionTracker().isLastOnGround() && data.getPositionTracker().isAirBelow()),

    GHOST_WALL(data -> data.getPositionTracker().isMathCollision());

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }
}
