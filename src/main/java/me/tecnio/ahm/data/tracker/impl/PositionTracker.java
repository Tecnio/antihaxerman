package me.tecnio.ahm.data.tracker.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientPosition;
import ac.artemis.packet.wrapper.server.PacketPlayServerPosition;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;
import lombok.Getter;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.Tracker;
import me.tecnio.ahm.update.PositionUpdate;
import me.tecnio.ahm.util.math.MathUtil;
import me.tecnio.ahm.util.mcp.AxisAlignedBB;
import me.tecnio.ahm.util.mcp.MathHelper;
import me.tecnio.ahm.util.player.Teleport;
import me.tecnio.ahm.util.player.TickTimer;
import me.tecnio.ahm.util.type.EvictingList;
import me.tecnio.ahm.util.world.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.function.Predicate;

@Getter
public final class PositionTracker extends Tracker {

    private double x, y, z,
            lastX, lastY, lastZ,
            lastLastX, lastLastY, lastLastZ;

    private double deltaX, deltaY, deltaZ, deltaXZ,
            lastDeltaX, lastDeltaZ, lastDeltaY, lastDeltaXZ,
            lastLastDeltaX, lastLastDeltaY, lastLastDeltaZ, lastLastDeltaXZ,
            acceleration, lastAcceleration;

    private int delayedFlyingTicks;

    private boolean onGround, lastOnGround, lastLastOnGround;
    private boolean serverGround, lastServerGround;

    private boolean position, lastPosition, lastLastPosition;

    private Location location, lastLocation, lastLastLocation;

    private boolean water, lastWater;
    private boolean lava, lastLava;
    private boolean web, lastWeb;
    private boolean underBlock, lastUnderBlock;
    private boolean piston, lastPiston;
    private boolean climbable, lastClimbable;
    private boolean slime, lastSlime;
    private boolean soulSand, lastSoulSand;
    private boolean ice, lastIce;
    private boolean wall, lastWall;
    private boolean fucked, lastFucked;
    private boolean airBelow;

    private float slipperiness;

    private final Predicate<Double> collision = position -> position % 0.015625D < 1.0E-8D;
    private boolean mathCollision;

    private boolean boat;

    private final EvictingList<Teleport> teleportQueue = new EvictingList<>(800);
    private boolean teleported;

    private final TickTimer ticksSinceTeleport = new TickTimer(this.data);

    public PositionTracker(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            final PacketPlayClientFlying wrapper = ((PacketPlayClientFlying) packet);

            ++this.delayedFlyingTicks;

            this.lastLastPosition = this.lastPosition;
            this.lastPosition = this.position;
            this.position = wrapper.isPos();

            this.lastLastOnGround = this.lastOnGround;
            this.lastOnGround = this.onGround;
            this.onGround = wrapper.isOnGround();

            this.lastLastX = this.lastX;
            this.lastLastY = this.lastY;
            this.lastLastZ = this.lastZ;

            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;

            if (packet instanceof PacketPlayClientPosition) {
                final PacketPlayClientPosition position = ((PacketPlayClientPosition) packet);

                this.x = position.getX();
                this.y = position.getY();
                this.z = position.getZ();
            }

            this.lastLastLocation = this.lastLocation;
            this.lastLocation = this.location;
            this.location = new Location(data.getPlayer().getWorld(), x, y, z);

            this.lastLastDeltaX = this.lastDeltaX;
            this.lastLastDeltaY = this.lastDeltaY;
            this.lastLastDeltaZ = this.lastDeltaZ;

            this.lastDeltaX = this.deltaX;
            this.lastDeltaY = this.deltaY;
            this.lastDeltaZ = this.deltaZ;

            this.deltaX = this.x - this.lastX;
            this.deltaY = this.y - this.lastY;
            this.deltaZ = this.z - this.lastZ;

            this.lastLastDeltaXZ = this.lastDeltaXZ;
            this.lastDeltaXZ = this.deltaXZ;
            this.deltaXZ = MathUtil.hypot(this.deltaX, this.deltaZ);

            this.lastAcceleration = this.acceleration;
            this.acceleration = this.deltaXZ - this.lastDeltaXZ;

            teleport: {
                this.teleported = false;

                if (!(wrapper.isPos() && wrapper.isLook() && !wrapper.isOnGround())) break teleport;

                if (this.teleportQueue.size() != 0) {
                    final Teleport teleport = this.teleportQueue.peek();

                    final Set<GPacketPlayServerPosition.PlayerTeleportFlags> flags = teleport.getFlags();

                    final boolean x = flags.contains(GPacketPlayServerPosition.PlayerTeleportFlags.X)
                            ? Math.abs((this.lastX + teleport.getX()) - this.x) < 1.0E-12
                            : teleport.getX() == this.x;

                    final boolean y = flags.contains(GPacketPlayServerPosition.PlayerTeleportFlags.Y)
                            ? Math.abs((this.lastY + teleport.getY()) - this.y) < 1.0E-12
                            : teleport.getY() == this.y;

                    final boolean z = flags.contains(GPacketPlayServerPosition.PlayerTeleportFlags.Z)
                            ? Math.abs((this.lastZ + teleport.getZ()) - this.z) < 1.0E-12
                            : teleport.getZ() == this.z;

                    if (x && y && z) {
                        this.teleported = true;
                        this.ticksSinceTeleport.reset();

                        data.setTicks(data.getTicks() - 1);

                        this.teleportQueue.poll();
                    }
                }
            }

            this.handleCollisions();

            data.setPositionUpdate(new PositionUpdate(this.x, this.y, this.z, this.lastX, this.lastY, this.lastZ,
                    this.deltaX, this.deltaY, this.deltaZ, this.lastDeltaX, this.lastDeltaY, this.lastDeltaZ,
                    this.onGround, this.lastOnGround, this.position, this.lastPosition));
        }

        else if (packet instanceof PacketPlayServerPosition) {
            final GPacketPlayServerPosition wrapper = ((GPacketPlayServerPosition) packet);

            data.getConnectionTracker().confirm(() -> this.teleportQueue.add(
                    new Teleport(wrapper.getX(), wrapper.getY(), wrapper.getZ(), wrapper.getFlags())));
        }
    }

    @Override
    public void handlePost(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying
                && ((PacketPlayClientFlying) packet).isPos()) {
            this.delayedFlyingTicks = 0;
        }
    }

    private void handleCollisions() {
        this.reset();

        final AxisAlignedBB boundingBox = new AxisAlignedBB(
                this.x - 0.3F, this.y, this.z - 0.3F,
                this.x + 0.3F, this.y + 1.8F, this.z + 0.3F
        );

        this.mathCollision = this.collision.test(boundingBox.minX) || this.collision.test(boundingBox.minZ)
                || this.collision.test(boundingBox.maxX) || this.collision.test(boundingBox.maxZ);

        final int minX = MathHelper.floor_double(boundingBox.minX - 1.0);
        final int maxX = MathHelper.floor_double(boundingBox.maxX + 1.0);
        final int minY = MathHelper.floor_double(boundingBox.minY - 1);
        final int maxY = MathHelper.floor_double(boundingBox.maxY + 1.0);
        final int minZ = MathHelper.floor_double(boundingBox.minZ - 1.0);
        final int maxZ = MathHelper.floor_double(boundingBox.maxZ + 1.0);

        for (int blockX = minX; blockX <= maxX; ++blockX) {
            for (int blockY = minY; blockY <= maxY; ++blockY) {
                for (int blockZ = minZ; blockZ <= maxZ; ++blockZ) {
                    final Block block = BlockUtil.getBlockAsync(new Location(data.getPlayer().getWorld(), blockX, blockY, blockZ));

                    if (block == null) continue;

                    final Material material = block.getType();
                    final String name = material.name();

                    this.water |= material == Material.WATER || material == Material.STATIONARY_WATER;
                    this.lava |= material == Material.LAVA || material == Material.STATIONARY_LAVA;
                    this.web |= material == Material.WEB;
                    this.piston |= material == Material.PISTON_BASE || material == Material.PISTON_EXTENSION
                            || material == Material.PISTON_MOVING_PIECE || material == Material.PISTON_STICKY_BASE;
                    this.fucked |= name.contains("STAIR") || name.contains("PATH") || name.contains("SNOW") || name.contains("SLAB")
                            || name.contains("STEP") || name.contains("CARPET") || name.contains("CHEST") || name.contains("DOOR")
                            || name.contains("ENDER") || name.contains("SLIME") || name.contains("LILY") || name.contains("COMPARATOR")
                            || name.contains("REPEATER") || name.contains("FENCE") || name.contains("BREWING") || name.contains("SKULL")
                            || name.contains("HEAD");

                    // This checks for blocks potentially above the players head.
                    if (blockY >= Math.floor(boundingBox.maxY)) {
                        this.underBlock |= material != Material.AIR && !BlockUtil.isLiquid(material);
                    }

                    // This checks for blocks below the players feet.
                    if (blockY <= boundingBox.minY) {
                        this.serverGround |= material != Material.AIR && !BlockUtil.isLiquid(material);
                    }

                    if (material != Material.AIR && !BlockUtil.isLiquid(material)) {
                        final int floor = (int) Math.floor(this.y);
                        final int ceiling = floor + 2;

                        final Location location = block.getLocation();

                        this.wall |= location.getBlockY() >= floor && location.getBlockY() <= ceiling;
                    }
                }
            }
        }

        try {
            this.boat = this.data.getPlayer().getNearbyEntities(2, 2, 2)
                    .stream().anyMatch(entity -> entity.getType() == EntityType.BOAT);
        } catch (final Exception ignored) {
            // who?
        }

        climbable: {
            final Block block = BlockUtil.getBlockAsync(new Location(data.getPlayer().getWorld(),
                    this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ())
            );

            if (block == null) break climbable;

            final Material material = block.getType();

            this.lastClimbable = this.climbable;
            this.climbable = material == Material.LADDER || material == Material.VINE;
        }

        below: {
            final Block blockBelow = BlockUtil.getBlockAsync(new Location(
                    data.getPlayer().getWorld(),
                    Math.floor(x), Math.floor(y - 1), Math.floor(z)
            ));

            if (blockBelow == null) break below;

            final Material material = blockBelow.getType();

            this.slime |= material == Material.SLIME_BLOCK;
            this.soulSand |= material == Material.SOUL_SAND;
            this.ice |= material == Material.ICE || material == Material.PACKED_ICE;
            this.airBelow |= material == Material.AIR;
        }

        slipperiness: {
            final Block blockBelow = BlockUtil.getBlockAsync(new Location(
                    data.getPlayer().getWorld(),
                    Math.floor(this.lastX), Math.floor(this.lastY - 1), Math.floor(this.lastZ)
            ));

            this.slipperiness = blockBelow == null ? 0.6F : this.getSlipperinessFromMaterial(blockBelow.getType());
        }
    }

    private void reset() {
        this.boat = false;

        this.lastWater = water;
        this.water = false;

        this.lastLava = lava;
        this.lava = false;

        this.lastWeb = web;
        this.web = false;

        this.lastUnderBlock = underBlock;
        this.underBlock = false;

        this.lastPiston = piston;
        this.piston = false;

        this.lastSlime = slime;
        this.slime = false;

        this.lastSoulSand = this.soulSand;
        this.soulSand = false;

        this.lastIce = this.ice;
        this.ice = false;

        this.lastWall = this.wall;
        this.wall = false;

        this.lastFucked = this.fucked;
        this.fucked = false;

        this.lastServerGround = this.serverGround;
        this.serverGround = false;

        this.airBelow = false;
    }

    private float getSlipperinessFromMaterial(final Material material) {
        switch (material) {
            case SLIME_BLOCK: return 0.8F;
            case ICE:
            case PACKED_ICE: return 0.98F;
            default: return 0.6F;
        }
    }
}
