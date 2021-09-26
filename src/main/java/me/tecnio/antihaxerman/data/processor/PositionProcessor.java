

package me.tecnio.antihaxerman.data.processor;

import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.PlayerUtil;
import me.tecnio.antihaxerman.util.type.BoundingBox;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vehicle;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class PositionProcessor {

    private final PlayerData data;

    private double x, y, z,
            lastX, lastY, lastZ,
            lastlastX, lastlastZ,
            deltaX, deltaY, deltaZ, deltaXZ,
            lastDeltaX, lastDeltaZ, lastDeltaY, lastDeltaXZ;

    private BoundingBox boundingBox;

    private long lastMovePacket;

    private float jumpPadTime;
    private boolean flying, jumping, inVehicle, inWater, inLava, inLiquid, fullySubmergedInLiquidStat, inAir, inWeb,
            blockNearHead, wasblockNearHead, onClimbable, onSolidGround, nearVehicle, onSlime,
            onIce, aroundIce, nearPiston, nearStair, nearCactus;

    private int ticks, airTicks, clientAirTicks, sinceVehicleTicks, sinceFlyingTicks,
            liquidTicks, sinceLiquidTicks, climbableTicks, sinceClimbableTicks,
            webTicks, sinceWebTicks,
            groundTicks, teleportTicks, sinceTeleportTicks, sinceSlimeTicks, solidGroundTicks,
            iceTicks, slimeTicks, sinceIceTicks, sinceJumpingTicks, sinceGroundTicks, sinceBlockNearHeadTicks;

    private boolean onGround, lastOnGround, mathematicallyOnGround;

    private final Deque<Vector> teleportList = new ArrayDeque<>();
    private boolean teleported;

    private final List<Block> blocks = new ArrayList<>();
    private List<Block> blocksBelow = new ArrayList<>();
    private List<Block> blocksAbove = new ArrayList<>();

    private List<Entity> nearbyEntities = new ArrayList<>();

    public PositionProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final WrappedPacketInFlying wrapper) {
        teleported = false;
        data.setCurrentTicks(data.getCurrentTicks() + 1);
        lastMovePacket = System.currentTimeMillis();
        this.lastOnGround = this.onGround;
        this.onGround = wrapper.isOnGround();

        if (wrapper.isPosition()) {
            lastlastX = lastX;
            lastlastZ = lastZ;
            lastX = this.x;
            lastY = this.y;
            lastZ = this.z;

            this.x = wrapper.getX();
            this.y = wrapper.getY();
            this.z = wrapper.getZ();

            lastDeltaX = deltaX;
            lastDeltaY = deltaY;
            lastDeltaZ = deltaZ;
            lastDeltaXZ = deltaXZ;

            deltaX = this.x - lastX;
            deltaY = this.y - lastY;
            deltaZ = this.z - lastZ;
            deltaXZ = Math.hypot(deltaX, deltaZ);

            mathematicallyOnGround = y % 0.015625 == 0.0;

            handleCollisions();

            if (wrapper.isLook()) {
                for (final Vector wantedLocation : teleportList) {
                    final boolean same = wantedLocation.getX() == x
                            || wantedLocation.getY() == y
                            || wantedLocation.getZ() == z;

                    if (same) {
                        teleported = true;
                        teleportTicks = 0;
                        sinceTeleportTicks = 0;

                        teleportList.remove(wantedLocation);
                        break;
                    }
                }
            }
        }

        handleTicks();
    }

    public void handleTicks() {
        ++this.ticks;
        if (onGround) ++groundTicks;
        else groundTicks = 0;

        if (inAir) {
            ++airTicks;
        } else {
            airTicks = 0;
        }

        if (!onGround) {
            ++clientAirTicks;
        } else {
            clientAirTicks = 0;
        }

        ++teleportTicks;

        if (data.getPlayer().getVehicle() != null) {
            sinceVehicleTicks = 0;
            inVehicle = true;
        } else {
            ++sinceVehicleTicks;
            inVehicle = false;
        }

        if (onIce) {
            ++iceTicks;
            sinceIceTicks = 0;
        } else {
            iceTicks = 0;
            ++sinceIceTicks;
        }

        if (onSlime) {
            ++slimeTicks;
            sinceSlimeTicks  = 0;
        } else {
            slimeTicks = 0;
            ++sinceSlimeTicks;
        }

        if(onGround) {
            sinceGroundTicks = 0;
        } else {
            ++sinceGroundTicks;
        }
        ++sinceTeleportTicks;

        if (onSolidGround) {
            ++solidGroundTicks;
        } else {
            solidGroundTicks = 0;
        }

        if (data.getPlayer().isFlying()) {
            flying = true;
            sinceFlyingTicks = 0;
        } else {
            ++sinceFlyingTicks;
            flying = false;
        }
        final boolean deltaModulo = deltaY % 0.015625 == 0.0;
        final boolean lastGround = lastY % 0.015625 == 0.0;
        final boolean step = deltaModulo && lastGround;
        final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
        final double expectedJumpMotion = 0.42F + modifierJump;
        if(Math.abs(expectedJumpMotion - deltaY) < 1E-5 && !onGround && lastOnGround && !step) {
            jumping = true;
            sinceJumpingTicks = 0;
        } else {
            ++sinceJumpingTicks;
            jumping = false;
        }

        if (blockNearHead) {
            sinceBlockNearHeadTicks = 0;
        } else {
            ++sinceBlockNearHeadTicks;
        }

        if (inLiquid) {
            ++liquidTicks;
            sinceLiquidTicks = 0;
        } else {
            liquidTicks = 0;
            ++sinceLiquidTicks;
        }

        if (onClimbable) {
            ++climbableTicks;
            sinceClimbableTicks = 0;
        } else {
            climbableTicks = 0;
            ++sinceClimbableTicks;
        }

        if (inWeb) {
            ++webTicks;
            sinceWebTicks = 0;
        } else {
            webTicks = 0;
            ++sinceWebTicks;
        }
    }

    public void handleCollisions() {
        blocks.clear();
        final BoundingBox boundingBox = new BoundingBox(data)
                .expandSpecific(0, 0, 0.55, 0.6, 0, 0);
        this.boundingBox = boundingBox;
        final double minX = boundingBox.getMinX();
        final double minY = boundingBox.getMinY();
        final double minZ = boundingBox.getMinZ();
        final double maxX = boundingBox.getMaxX();
        final double maxY = boundingBox.getMaxY();
        final double maxZ = boundingBox.getMaxZ();

        for (double x = minX; x <= maxX; x += (maxX - minX)) {
            for (double y = minY; y <= maxY + 0.01; y += (maxY - minY) / 4) {
                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                    final Location location = new Location(data.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);

                    if (block != null) {
                        blocks.add(block);
                    }
                }
            }
        }

        handleClimbableCollision();
        handleNearbyEntities();

        inLiquid = blocks.stream().anyMatch(Block::isLiquid);
        fullySubmergedInLiquidStat = blocks.stream().allMatch(block -> block.getType() == Material.STATIONARY_WATER || block.getType() == Material.STATIONARY_LAVA);
        inWater = blocks.stream().anyMatch(block -> block.getType().toString().contains("WATER"));
        inLava = blocks.stream().anyMatch(block -> block.getType().toString().contains("LAVA"));
        inWeb = blocks.stream().anyMatch(block -> block.getType().toString().contains("WEB"));
        inAir = blocks.stream().allMatch(block -> block.getType() == Material.AIR) && nearbyEntities.stream().noneMatch(entity -> entity.getType() == EntityType.BOAT);
        onIce = blocks.stream().anyMatch(block -> block.getType().toString().contains("ICE"));
        onSolidGround = blocks.stream().anyMatch(block -> block.getType().isSolid());
        nearStair = blocks.stream().anyMatch(block -> block.getType().toString().contains("STAIR"));
        nearCactus = blocks.stream().anyMatch(block -> block.getType() == Material.CACTUS);
        if(!blocks.stream().filter(block -> block.getLocation().getY() - data.getPositionProcessor().getY() >= 1.0).anyMatch(block -> block.getType() != Material.AIR)) {
            if(blockNearHead) {
                wasblockNearHead = true;
            }
        }
        blockNearHead = blocks.stream().filter(block -> block.getLocation().getY() - data.getPositionProcessor().getY() >= 1.0).anyMatch(block -> block.getType() != Material.AIR);
        blocksAbove = blocks.stream().filter(block -> block.getLocation().getY() - data.getPositionProcessor().getY() >= 1.0).collect(Collectors.toList());
        blocksBelow = blocks.stream().filter(block -> block.getLocation().getY() - data.getPositionProcessor().getY() < 0.0).collect(Collectors.toList());
        onSlime = blocks.stream().anyMatch(block -> block.getType().toString().equalsIgnoreCase("SLIME_BLOCK"));
        nearPiston = blocks.stream().anyMatch(block -> block.getType().toString().contains("PISTON"));
    }

    public void handleClimbableCollision() {
        final int var1 = NumberConversions.floor(this.x);
        final int var2 = NumberConversions.floor(this.y);
        final int var3 = NumberConversions.floor(this.z);

        final Block var4 = this.getBlock(new Location(data.getPlayer().getWorld(), var1, var2, var3));

        if (var4 != null) {
            this.onClimbable = var4.getType() == Material.LADDER || var4.getType() == Material.VINE;
        }
    }

    public void handleNearbyEntities() {
        nearbyEntities = PlayerUtil.getEntitiesWithinRadius(data.getPlayer().getLocation(), 2);
        nearVehicle = nearbyEntities.stream().anyMatch(entity -> entity instanceof Vehicle) || nearbyEntities.stream().anyMatch(entity -> entity instanceof Boat);
    }

    public void handleTeleport(final WrappedPacketOutPosition wrapper) {
        final Vector requestedLocation = new Vector(
                wrapper.getPosition().x,
                wrapper.getPosition().y,
                wrapper.getPosition().z
        );

        teleportList.add(requestedLocation);
    }

    public void handleClientCommand(final WrappedPacketInClientCommand wrapper) {
        if (wrapper.getClientCommand() == WrappedPacketInClientCommand.ClientCommand.PERFORM_RESPAWN) {

        }
    }

    public boolean isColliding(final CollisionType collisionType, final Material blockType) {
        if (collisionType == CollisionType.ALL) {
            return blocks.stream().allMatch(block -> block.getType() == blockType);
        }
        return blocks.stream().anyMatch(block -> block.getType() == blockType);
    }

    public Block getBlock(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getWorld().getBlockAt(location);
        } else {
            return null;
        }
    }

    public enum CollisionType {
        ANY, ALL
    }
}
