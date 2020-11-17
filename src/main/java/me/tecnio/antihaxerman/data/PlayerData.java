/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

package me.tecnio.antihaxerman.data;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketEvent;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import lombok.Setter;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckManager;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.utils.data.EvictingList;
import me.tecnio.antihaxerman.utils.data.Pair;
import me.tecnio.antihaxerman.utils.math.MathUtils;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;
import me.tecnio.antihaxerman.utils.player.CollisionUtils;
import me.tecnio.antihaxerman.utils.world.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

@Getter @Setter
public final class PlayerData {
    private final UUID uuid;
    private final Player player;

    private final Random random = new Random();

    private final List<Check> checks;

    private int tick, airTick, airTicks, serverAirTick, serverAirTicks, groundTick, velocityTicks, groundTicks, serverGroundTick, serverGroundTicks, collidedVTick, collidedVTicks, collidedHTick, collidedHTicks, digTick, iceTick, iceTicks, slimeTick, slimeTicks, touchingAirTicks, liquidTick, liquidTicks, climbableTick, climbableTicks, teleportTick, flyingTick, flyingTicks, blockingTick, blockingTicks, sneakingTick, sneakingTicks, sprintingTick, sprintingTicks, attackTick, pistonTick, halfBlockTick, touchingAirTick;

    private long lastFlying, lastLastFlying;
    private boolean inWeb, inLiquid, underBlock, onSlime, onGround, onServerGround, touchingAir, inVoid, nearBoat, onClimbable;
    private List<Block> collidedBlocks = new ArrayList<>();

    private int velocityTick, maxVelocityTicks, velocityID;
    private boolean verifyingVelocity;

    private boolean alerts, verbose;
    private long lastAlertMessage;

    private double sensitivity;
    private float gcd;
    private int sensitivityAsPercentage;
    private boolean usingCinematic;

    private int movements;
    private double cps;
    private final EvictingList<Integer> clicks = new EvictingList<>(10);

    private Location location, lastLocation;
    private double deltaX, deltaY, deltaZ, deltaXZ, lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;
    private float deltaYaw, lastDeltaYaw, deltaPitch, lastDeltaPitch;
    private boolean sprinting, sneaking, digging, placing, blocking;
    private Vector lastVelocity = new Vector(0, 0, 0);

    private Vector direction = new Vector();

    private EvictingList<Pair<Location, Integer>> targetLocations = new EvictingList<>(40);
    private Entity target;

    private int transactionPing;

    private Map<Short, Long> transactionUpdates = new HashMap<>();

    public PlayerData(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();

        location = player.getLocation();
        lastLocation = player.getLocation();

        checks = CheckManager.loadChecks(this);
    }

    public void toggleAlerts() {
        AlertManager.toggleAlerts(this);
        alerts = !alerts;
    }

    public void toggleVerbose() {
        AlertManager.toggleVerbose(this);
        verbose = !verbose;
    }

    public int getKeepAlivePing() {
        return PacketEvents.getAPI().getPlayerUtils().getPing(player);
    }

    public boolean isLagging() {
        final long diff = Math.abs(lastFlying - lastLastFlying);
        return (diff > 80 || diff < 30);
    }

    public boolean isTakingVelocity() { return velocityTicks() < maxVelocityTicks; }

    public int velocityTicks() { return Math.abs(tick - velocityTick); }

    public int airTicks() { return Math.abs(tick - airTick); }

    public int attackTicks() { return Math.abs(tick - attackTick); }

    public int serverAirTicks() { return Math.abs(tick - serverAirTick); }

    public int groundTicks() { return Math.abs(tick - groundTick); }

    public int serverGroundTicks() { return Math.abs(tick - serverGroundTick); }

    public int collidedVTicks() { return Math.abs(tick - collidedVTick); }

    public int collidedHTicks() { return Math.abs(tick - collidedHTick); }

    public int digTicks() { return Math.abs(tick - digTick); }

    public int iceTicks() { return Math.abs(tick - iceTick); }

    public int slimeTicks() { return Math.abs(tick - slimeTick); }

    public int liquidTicks() { return Math.abs(tick - liquidTick); }

    public int climbableTicks() { return Math.abs(tick - climbableTick); }

    public int teleportTicks() { return Math.abs(tick - teleportTick); }

    public int flyingTicks() {
        return Math.abs(tick - flyingTick);
    }

    public int blockingTicks() { return Math.abs(tick - blockingTick); }

    public int sneakingTicks() { return Math.abs(tick - sneakingTick); }

    public int sprintingTicks() { return Math.abs(tick - sprintingTick); }

    public int pistonTicks() { return Math.abs(tick - pistonTick); }

    public int halfBlockTicks() { return Math.abs(tick - halfBlockTick); }

    public void onPacketReceive(final PacketReceiveEvent event) {
        process(event);

        if (PacketUtils.isFlyingPacket(event.getPacketId())) onFlying();
        if (PacketUtils.isPositionPacket(event.getPacketId())) onMove();
        if (PacketUtils.isRotationPacket(event.getPacketId())) onRotation();
        if (event.getPacketId() == PacketType.Client.USE_ENTITY) onAttack(event);
        AntiHaxerman.getCheckExecutor().execute(() -> checks.stream().filter(check -> check.enabled).forEach(check -> check.onPacketReceive(event)));
    }

    public void onPacketSend(final PacketSendEvent event) {
        process(event);

        AntiHaxerman.getCheckExecutor().execute(() -> checks.stream().filter(check -> check.enabled).forEach(check -> check.onPacketSend(event)));
    }

    public void onFlying() {
        AntiHaxerman.getCheckExecutor().execute(() -> checks.stream().filter(check -> check.enabled).forEach(Check::onFlying));
    }

    public void onMove() {
        AntiHaxerman.getCheckExecutor().execute(() -> checks.stream().filter(check -> check.enabled).forEach(Check::onMove));
    }

    public void onRotation() {
        AntiHaxerman.getCheckExecutor().execute(() -> checks.stream().filter(check -> check.enabled).forEach(Check::onRotation));
    }

    public void onAttack(PacketReceiveEvent event) {
        final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(event.getNMSPacket());

        AntiHaxerman.getCheckExecutor().execute(() -> checks.stream().filter(check -> check.enabled).forEach(check -> check.onAttack(wrapper)));
    }

    private void process(final PacketEvent e) {
        if (e instanceof PacketReceiveEvent) {
            final PacketReceiveEvent event = ((PacketReceiveEvent) e);

            if (PacketUtils.isFlyingPacket(event.getPacketId())) {
                lastLastFlying = lastFlying;
                lastFlying = System.currentTimeMillis();

                final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(event.getNMSPacket());

                double x = location.getX(), y = location.getY(), z = location.getZ();
                float yaw = location.getYaw(), pitch = location.getPitch();
                boolean onGround = this.onGround;

                if (wrapper.isPosition()) {
                    x = wrapper.getX();
                    y = wrapper.getY();
                    z = wrapper.getZ();

                    onGround = wrapper.isOnGround();
                }

                if (wrapper.isLook()) {
                    yaw = wrapper.getYaw();
                    pitch = wrapper.getPitch();
                }

                final Location location = new Location(player.getWorld(), x, y, z, yaw, pitch);
                final Location lastLocation = this.getLocation();

                processLocation(location, lastLocation, onGround);
            } else if (event.getPacketId() == PacketType.Client.ENTITY_ACTION) {
                final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(event.getNMSPacket());

                switch (wrapper.getAction()) {
                    case START_SPRINTING:
                        sprinting = true;
                        break;
                    case STOP_SPRINTING:
                        sprinting = false;
                        break;
                    case START_SNEAKING:
                        sneaking = true;
                        break;
                    case STOP_SNEAKING:
                        sneaking = false;
                        break;
                }
            } else if (event.getPacketId() == PacketType.Client.TRANSACTION) {
                final WrappedPacketInTransaction wrapper = new WrappedPacketInTransaction(event.getNMSPacket());

                if (wrapper.getActionNumber() == velocityID && verifyingVelocity) {
                    velocityTick = tick;
                    verifyingVelocity = false;
                    maxVelocityTicks = (int) (((lastVelocity.getX() + lastVelocity.getZ()) / 2D + 2D) * 15D);
                    velocityTicks = 0;
                }
            } else if (event.getPacketId() == PacketType.Client.USE_ENTITY) {
                final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(event.getNMSPacket());

                attackTick = tick;

                if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                    final Entity target = wrapper.getEntity();

                    if (this.target != target) {
                        this.target = target;

                        targetLocations.clear();
                    }
                }
            } else if (event.getPacketId() == PacketType.Client.BLOCK_DIG) {
                final WrappedPacketInBlockDig wrapper = new WrappedPacketInBlockDig(event.getNMSPacket());

                switch (wrapper.getDigType()) {
                    case START_DESTROY_BLOCK:
                        digging = true;
                        break;

                    case ABORT_DESTROY_BLOCK:
                    case STOP_DESTROY_BLOCK:
                        digging = false;
                        break;

                    case RELEASE_USE_ITEM:
                        blocking = false;
                        break;
                }
            } else if (event.getPacketId() == PacketType.Client.BLOCK_PLACE) {
                placing = true;

                if (player.getItemInHand().toString().toLowerCase().contains("sword")) {
                    blocking = true;
                }
            } else if (event.getPacketId() == PacketType.Client.ARM_ANIMATION) {
                click: {
                    if (digging || movements > 5) break click;

                    clicks.add(movements);
                }

                if (clicks.size() > 5) {
                    cps = MathUtils.getCps(clicks);
                }

                movements = 0;
            }
        }

        if (e instanceof PacketSendEvent) {
            final PacketSendEvent event = ((PacketSendEvent) e);

            if (event.getPacketId() == PacketType.Server.ENTITY_VELOCITY) {
                final WrappedPacketOutEntityVelocity wrapper = new WrappedPacketOutEntityVelocity(event.getNMSPacket());

                if (wrapper.getEntityId() == player.getEntityId()) {
                    lastVelocity = new Vector(wrapper.getVelocityX(), wrapper.getVelocityY(), wrapper.getVelocityZ());

                    verifyingVelocity = true;
                    velocityID = random.nextInt(32767);

                    PacketEvents.getAPI().getPlayerUtils().sendPacket(player, new WrappedPacketOutTransaction(0, (short) velocityID, false));
                }
            } else if (event.getPacketId() == PacketType.Server.POSITION) {
                teleportTick = tick;
            }
        }
    }

    private void processLocation(Location location, Location lastLocation, boolean onGround) {
        ++this.tick;

        this.lastLocation = lastLocation;
        this.location = location;

        double lastDeltaX = deltaX;
        double deltaX = location.getX() - lastLocation.getX();

        this.lastDeltaX = lastDeltaX;
        this.deltaX = deltaX;

        double lastDeltaY = deltaY;
        double deltaY = location.getY() - lastLocation.getY();

        this.lastDeltaY = lastDeltaY;
        this.deltaY = deltaY;

        double lastDeltaZ = deltaZ;
        double deltaZ = location.getZ() - lastLocation.getZ();

        this.lastDeltaZ = lastDeltaZ;
        this.deltaZ = deltaZ;

        double lastDeltaXZ = deltaXZ;
        double deltaXZ = Math.hypot(deltaX, deltaZ);

        this.lastDeltaXZ = lastDeltaXZ;
        this.deltaXZ = deltaXZ;

        float lastDeltaYaw = deltaYaw;
        float deltaYaw = Math.abs(MathUtils.getAngleDiff(location.getYaw(), lastLocation.getYaw()));

        this.lastDeltaYaw = lastDeltaYaw;
        this.deltaYaw = deltaYaw;

        float lastDeltaPitch = deltaPitch;
        float deltaPitch = Math.abs(location.getPitch() - lastLocation.getPitch());

        this.lastDeltaPitch = lastDeltaPitch;
        this.deltaPitch = deltaPitch;

        direction = new Vector(-Math.sin(player.getEyeLocation().getYaw() * Math.PI / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(player.getEyeLocation().getYaw() * Math.PI / 180.0F) * (float) 1 * 0.5F);

        if (!player.getItemInHand().toString().toLowerCase().contains("sword")) {
            blocking = false;
        }

        inVoid = location.getY() < 1;
        nearBoat = CollisionUtils.onBoat(this);

        placing = false;

        collidedBlocks = CollisionUtils.handleCollisions(this);

        if (isTakingVelocity()) {
            velocityTicks++;
        } else velocityTicks = 0;

        if (player.isFlying()) {
            flyingTick = tick;
            flyingTicks++;
        } else flyingTicks = 0;

        if (onGround) {
            airTicks = 0;
            groundTick = tick;
            groundTicks++;
            this.onGround = true;
        } else {
            groundTicks = 0;
            airTick = tick;
            airTicks++;
            this.onGround = false;
        }

        if (CollisionUtils.isOnGround(this)) {
            serverAirTicks = 0;
            serverGroundTick = tick;
            serverGroundTicks++;
            onServerGround = true;
        } else {
            serverGroundTicks = 0;
            serverAirTick = tick;
            serverAirTicks++;
            onServerGround = false;
        }

        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE")
                || player.getLocation().clone().add(0, -0.5, 0).getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE")) {
            iceTick = tick;
            iceTicks++;
        } else iceTicks = 0;

        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("SLIME")
                || player.getLocation().clone().add(0, -0.5, 0).getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("SLIME")) {
            slimeTick = tick;
            slimeTicks++;
            onSlime = true;
        } else {
            slimeTicks = 0;
            onSlime = false;
        }

        for (Material m: BlockUtils.getNearbyBlocks(location, 3)) {
            String material = m.toString();

            if (material.contains("PISTON_")) {
                pistonTick = tick;
            } else if (material.contains("SLAB") || material.contains("SKULL") || material.contains("STAIR")) {
                halfBlockTick = tick;
            }
        }

        if (CollisionUtils.blockNearHead(this)) {
            collidedVTick = tick;
            collidedVTicks++;
            underBlock = true;
        } else {
            collidedVTicks = 0;
            underBlock = false;
        }

        if (CollisionUtils.nearWall(this)) {
            collidedHTick = tick;
            collidedHTicks++;
        } else collidedHTicks = 0;

        if (CollisionUtils.isInLiquid(this)) {
            liquidTick = tick;
            liquidTicks++;
            inLiquid = true;
        } else {
            liquidTicks = 0;
            inLiquid = false;
        }

        if (CollisionUtils.isOnClimbable(this)) {
            climbableTick = tick;
            climbableTicks++;
            onClimbable = true;
        } else {
            climbableTicks = 0;
            onClimbable = false;
        }

        if (CollisionUtils.isOnAir(this)) {
            touchingAirTick = tick;
            touchingAirTicks++;
            touchingAir = true;
        } else {
            touchingAirTicks = 0;
            touchingAir = false;
        }

        inWeb = CollisionUtils.isInWeb(this);

        if (blocking) {
            blockingTick = tick;
            blockingTicks++;
        } else blockingTicks = 0;

        if (sneaking) {
            sneakingTick = tick;
            sneakingTicks++;
        } else sneakingTicks = 0;

        if (sprinting) {
            sprintingTick = tick;
            sprintingTicks++;
        } else sprintingTicks = 0;
    }
}
