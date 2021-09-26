

package me.tecnio.antihaxerman.exempt.type;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.listener.bukkit.BukkitEventManager;
import me.tecnio.antihaxerman.manager.AFKManager;
import me.tecnio.antihaxerman.util.ServerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.util.NumberConversions;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Getter
public enum ExemptType {

    CHUNK(data -> !data.getPlayer().getWorld().isChunkLoaded(
            NumberConversions.floor(data.getPositionProcessor().getX()) >> 4,
            NumberConversions.floor(data.getPositionProcessor().getZ()) >> 4)
    ),

    TPS(data -> ServerUtil.getTPS() < 17.0D),

    HURT(data -> BukkitEventManager.dmg.contains(data.getPlayer().getUniqueId())),

    TELEPORT(data -> data.getPositionProcessor().isTeleported()),

    TELEPORT_DELAY(data -> data.getPositionProcessor().getTeleportTicks() < 5),

    UPWARDS_VEL(e -> e.getVelocityProcessor().getVelocityY() > 0.0 && e.getVelocityProcessor().isTakingVelocity()),

    VELOCITY(data -> data.getVelocityProcessor().isTakingVelocity()),

    PEARL(data -> {
       if(data.getEnderpearlTime() != 0) {
           if(System.currentTimeMillis() - data.getEnderpearlTime() < 5000L) {
               return true;
           }
           else {
               return false;
           }
       }
       return false;
    }),

//    public boolean isPearl(long enderpearlTime) {
//       if(enderpearlTime != 0) {
//           if(System.currentTimeMillis() - enderpearlTime < 5000L) {
//               return true;
//           }
//           else {
//               return false;
//           }
//       }
//       return false;
//    }

    NEAR_WALL(data -> data.getPlayer().isDead()),

    AFK(data -> AFKManager.INSTANCE.isAFK(data.getPlayer())),

    RESPAWN(data -> data.getRespawnTime() != 0 && System.currentTimeMillis() - data.getRespawnTime() < 5000L),

    NEARCACTUS(data -> data.getPositionProcessor().isNearCactus()),

    VELOCITY_ON_TICK(data -> data.getVelocityProcessor().getTicksSinceVelocity() < 3),

    SLIME(data -> data.getPositionProcessor().getSinceSlimeTicks() < 20),

    SLIME_ON_TICK(data -> data.getPositionProcessor().getSinceSlimeTicks() < 2),

    DEAD(data -> data.getPlayer().isDead()),

    NEARHORSE(data -> data.getPositionProcessor().getNearbyEntities().stream().anyMatch(entity -> entity.getType() == EntityType.HORSE)),

    FIRE(data -> data.getPlayer().getFireTicks() > 0),

    NEARSLIME(data -> {
        AtomicBoolean istrue = new AtomicBoolean(false);
        data.getPositionProcessor().getBlocks().forEach(block -> {
            if(block.getType().toString().contains("SLIME")) {
                istrue.set(true);
            }
        });
        return istrue.get();
    }),

    NEARANVIL(data -> {
        AtomicBoolean istrue = new AtomicBoolean(false);
        data.getPositionProcessor().getBlocks().forEach(block -> {
            if(block.getType().toString().contains("ANVIL")) {
                istrue.set(true);
            }
        });
        return istrue.get();
    }),

    DIGGING(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastDiggingTick() < 10),

    BLOCK_BREAK(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastBreakTick() < 10),

    PLACING(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastPlaceTick() < 10),

    BUKKIT_PLACING(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastBukkitPlaceTick() < 10),

    LONG_BUKKIT_PLACING(data -> AntiHaxerman.INSTANCE.getTickManager().getTicks() - data.getActionProcessor().getLastBukkitPlaceTick() < 25),

    BOAT(data -> data.getPositionProcessor().isNearVehicle()),

    VEHICLE(data -> data.getPositionProcessor().getSinceVehicleTicks() < 20),

    LIQUID(data -> data.getPositionProcessor().getSinceLiquidTicks() < 4),

    NEARSTAIRS(data -> {
        AtomicBoolean istrue = new AtomicBoolean(false);
        data.getPositionProcessor().getBlocksBelow().forEach(block -> {
            if(block.getType().toString().contains("STAIRS")) {
                istrue.set(true);
            }
        });
        return istrue.get();
    }),

    NEARSLABS(data -> {
        AtomicBoolean istrue = new AtomicBoolean(false);
        data.getPositionProcessor().getBlocksBelow().forEach(block -> {
            if(block.getType().toString().contains("SLAB")) {
                istrue.set(true);
            }
        });
        return istrue.get();
    }),

    NEARICE(data -> {
        AtomicBoolean istrue = new AtomicBoolean(false);
        data.getPositionProcessor().getBlocks().forEach(block -> {
            if(block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE) {
                istrue.set(true);
            }
        });
        return istrue.get();
    }),

    DROP(data -> data.getActionProcessor().getLastDropTick() > 10),

    ONBED(data -> {
        AtomicBoolean istrue = new AtomicBoolean(false);
        data.getPositionProcessor().getBlocks().forEach(block -> {
            if(block.getType().toString().contains("BED")) {
                istrue.set(true);
            }
        });
        return istrue.get();
    }),

    GETTINGCOMBOED(data -> data.getCombatProcessor().getHitTicks() <= 10),

    UNDERBLOCK(data -> data.getPositionProcessor().isBlockNearHead()),

    UNDERBLOCKWAS(data -> data.getPositionProcessor().getSinceBlockNearHeadTicks() <= 2),

    PISTON(data -> data.getPositionProcessor().isNearPiston()),

    VOID(data -> data.getPositionProcessor().getY() < 4),

    COMBAT(data -> data.getCombatProcessor().getHitTicks() < 5),

    FLYING(data -> data.getPositionProcessor().getSinceFlyingTicks() < 40),

    AUTOCLICKER(data -> data.getExemptProcessor().isExempt(ExemptType.PLACING, ExemptType.DIGGING, ExemptType.BLOCK_BREAK)),

    WEB(data -> data.getPositionProcessor().getSinceWebTicks() < 10),

    JOINED(data -> System.currentTimeMillis() - data.getJoinTime() < 5000L),

    LONG_JOINED(data -> System.currentTimeMillis() - data.getJoinTime() < 15000L),

    LAGGING(data -> {
        final long delta = data.getFlying() - data.getLastFlying();

        return delta > 100 || delta < 2;
    }),

    LAGGINGHARD(data -> {
        final int ping = PacketEvents.get().getPlayerUtils().getPing(data.getPlayer());

        return ping >= 200;
    }),

    CREATIVE(data -> data.getPlayer().getGameMode() == GameMode.CREATIVE),

    GHOST_BLOCK(data -> data.getGhostBlockProcessor().isOnGhostBlock()),

    WEBRN(data -> data.getPositionProcessor().isInWeb()),

    CINEMATIC(data -> data.getRotationProcessor().isCinematic()),

    CINEMATIC_TIME(data -> System.currentTimeMillis() - data.getRotationProcessor().getCinematicTime() < 5000L),

    CLIMBABLE(data -> data.getPositionProcessor().getSinceClimbableTicks() < 10),

    ICE(data -> data.getPositionProcessor().getSinceIceTicks() < 10);

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }
}
