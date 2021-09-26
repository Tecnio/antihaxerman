package me.tecnio.antihaxerman.check.impl.movement.strafe;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.ServerUtil;
import me.tecnio.antihaxerman.util.type.AABB;
import me.tecnio.antihaxerman.util.type.WrappedBlock;
import me.tecnio.antihaxerman.util.type.WrappedEntity;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.utils.player.Direction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

//@CheckInfo(name = "Strafe", type = "B", description = "Checks for invalid strafing.")
//public class StrafeB extends Check {
//
//    private final double THRESHOLD;
//    private final Map<UUID, Long> lastIdleTick;
//    private final Set<UUID> bouncedSet;
//    private final Set<UUID> wasSneakingOnEdgeSet;
//    private Set<Direction> boxSidesTouchingBlocks;
//
//    public StrafeB(PlayerData data) {
//        super(data);
//        THRESHOLD = 0.5;
//        this.lastIdleTick = new HashMap<>();
//        this.bouncedSet = new HashSet<>();
//        this.wasSneakingOnEdgeSet = new HashSet<>();
//    }
//
//    private boolean collidingHorizontally(WrappedPacketInFlying e) {
//        Location loc = new Location(data.getPlayer().getWorld(), e.getX(), e.getY(), e.getZ());
//        for(Direction dir : boxSidesTouchingBlocks = checkTouchingBlock(new AABB(loc.toVector().add(new Vector(-0.299999, 0.000001, -0.299999)), loc.toVector().add(new Vector(0.299999, 1.799999, 0.299999))), loc.getWorld(), 0.0001, 8)) {
//            if(dir == Direction.EAST || dir == Direction.NORTH || dir == Direction.SOUTH || dir == Direction.WEST) {
//                bouncedSet.add(data.getPlayer().getUniqueId());
//                return true;
//            }
//        }
//        bouncedSet.remove(data.getPlayer().getUniqueId());
//        return false;
//    }
//
//    private boolean testLiquid(Set<Material> mats) {
//        for(Material mat : mats) {
//            if(mat == Material.WATER || mat == Material.STATIONARY_WATER || mat == Material.LAVA || mat == Material.STATIONARY_LAVA)
//                return true;
//        }
//        return false;
//    }
//
//    private boolean isValidStrafe(double angle) {
//        double modulo = (angle % (Math.PI / 4)) * (4 / Math.PI); //scaled so that legit values should be close to either 0 or +/-1
//        double error = Math.abs(modulo - Math.round(modulo)) * (Math.PI / 4); //compute error (and then scale back to radians)
//        return error <= THRESHOLD; //in radians
//    }
//
//    public Set<Direction> checkTouchingBlock(AABB boundingBox, World world, double borderSize, int clientVersion) {
//        AABB bigBox = boundingBox.clone();
//        Vector min = bigBox.getMin().add(new Vector(-borderSize, -borderSize, -borderSize));
//        Vector max = bigBox.getMax().add(new Vector(borderSize, borderSize, borderSize));
//        Set<Direction> directions = EnumSet.noneOf(Direction.class);
//        //The coordinates should be floored, but this works too.
//        for (int x = (int) (min.getX() < 0 ? min.getX() - 1 : min.getX()); x <= max.getX(); x++) {
//            for (int y = (int) min.getY() - 1; y <= max.getY(); y++) { //always subtract 1 so that fences/walls can be checked
//                for (int z = (int) (min.getZ() < 0 ? min.getZ() - 1 : min.getZ()); z <= max.getZ(); z++) {
//                    Block b = ServerUtil.getBlockAsync(new Location(world, x, y, z));
//                    if (b != null) {
//                        WrappedBlock bNMS = WrappedBlock.getWrappedBlock(b, clientVersion);
//                        for (AABB blockBox : bNMS.getCollisionBoxes()) {
//                            if (blockBox.getMin().getX() > boundingBox.getMax().getX() && blockBox.getMin().getX() < bigBox.getMax().getX()) {
//                                directions.add(Direction.EAST);
//                            }
//                            if (blockBox.getMin().getY() > boundingBox.getMax().getY() && blockBox.getMin().getY() < bigBox.getMax().getY()) {
//                                directions.add(Direction.UP);
//                            }
//                            if (blockBox.getMin().getZ() > boundingBox.getMax().getZ() && blockBox.getMin().getZ() < bigBox.getMax().getZ()) {
//                                directions.add(Direction.SOUTH);
//                            }
//                            if (blockBox.getMax().getX() > bigBox.getMin().getX() && blockBox.getMax().getX() < boundingBox.getMin().getX()) {
//                                directions.add(Direction.WEST);
//                            }
//                            if (blockBox.getMax().getY() > bigBox.getMin().getY() && blockBox.getMax().getY() < boundingBox.getMin().getY()) {
//                                directions.add(Direction.DOWN);
//                            }
//                            if (blockBox.getMax().getZ() > bigBox.getMin().getZ() && blockBox.getMax().getZ() < boundingBox.getMin().getZ()) {
//                                directions.add(Direction.NORTH);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return directions;
//    }
//
//    private void prepareNextMove(Player pp, long currentTick, boolean sneakOnEdge) {
//        UUID uuid = pp.getUniqueId();
//
//        if(sneakOnEdge) {
//            wasSneakingOnEdgeSet.add(uuid);
//        } else {
//            wasSneakingOnEdgeSet.remove(uuid);
//        }
//    }
//
//    private float computeFriction() {
//        boolean flying = data.getPlayer().isFlying();
//
//        //patch some inconsistencies
//        //boolean teleportBug = pp.getCurrentTick() == pp.getLastTeleportAcceptTick();
//        //boolean onGround = teleportBug ? pp.isOnGroundReally() : pp.isOnGround();
//        boolean onGround = data.getPlayer().isOnGround();
//
//        if(data.getPositionProcessor().isInWater() && !flying) {
//            float friction = 0.8F;
//            float depthStrider = 0;
//            ItemStack boots = data.getPlayer().getInventory().getBoots();
//            if(boots != null) {
//                depthStrider = boots.getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
//            }
//            if(depthStrider > 3) {
//                depthStrider = 3;
//            }
//
//            if(!onGround) {
//                depthStrider *= 0.5F;
//            }
//
//            if(depthStrider > 0) {
//                friction += (0.546F - friction) * depthStrider / 3F;
//            }
//
//            return friction;
//        } else if (data.getPositionProcessor().isInLava() && !flying) {
//            return 0.5F;
//        } else {
//            float friction = 0.91F;
//
//            if (onGround) {
//                Vector pos = data.getPlayer().getLocation().clone().toVector();
//                Block b = ServerUtil.getBlockAsync(new Location(data.getPlayer().getWorld(), pos.getX(), pos.getY() - 1, pos.getZ()));
//                if (b != null) {
//                    friction *= WrappedBlock.getWrappedBlock(b, 8).getSlipperiness();
//                }
//            }
//            return friction;
//        }
//    }
//
//    @Override
//    public void handle(Packet packet) {
//        if(packet.isFlying() && !isExempt(ExemptType.VELOCITY)) {
//            WrappedPacketInFlying wrapped = new WrappedPacketInFlying(packet.getRawPacket());
//            boolean bounced = bouncedSet.contains(data.getPlayer().getUniqueId());
//            boolean collidingHorizontally = collidingHorizontally(wrapped);
//
//            Block footBlock = ServerUtil.getBlockAsync(data.getPlayer().getLocation().clone().add(0, -0.2, 0));
//            if(footBlock == null)
//                return;
//            double friction = computeFriction();
//
//            boolean sneakEdge = data.getPlayer().isSneaking() && !WrappedBlock.getWrappedBlock(footBlock, 8).isSolid() && data.getPlayer().isOnGround();
//
//            boolean wasSneakingOnEdge = wasSneakingOnEdgeSet.contains(data.getPlayer().getUniqueId());
//
//            Location lastLocation = new Location(data.getPlayer().getWorld(), data.getPositionProcessor().getLastX(), data.getPositionProcessor().getLastY(), data.getPositionProcessor().getLastZ());
//            Set<Material> collidedMats = WrappedEntityUtil.getWrappedEntity(data.getPlayer()).getCollisionBox(lastLocation.toVector()).getMaterials(data.getPlayer().getWorld());
//
//            boolean onSlimeblock = (data.getPositionProcessor().isLastOnGround() || data.getPositionProcessor().isOnGround()) && footBlock.getType() == Material.SLIME_BLOCK;
//            boolean nearLiquid = testLiquid(collidedMats);
//
//            double dX = data.getPositionProcessor().getDeltaX();
//            double dZ = data.getPositionProcessor().getDeltaZ();
//            dX /= friction;
//            dZ /= friction;
//
//            Vector accelDir = new Vector(dX, 0, dZ);
//            Vector yaw = getDirection(data.getRotationProcessor().getYaw(), 0);
//
//            if(isExempt(ExemptType.TELEPORT) || bounced || collidingHorizontally || sneakEdge || testJumped() || nearLiquid || //TODO get rid of e.isJump() from here and actually try to handle it|| collidedMats.contains(Material.LADDER) ||
//                    collidedMats.contains(Material.VINE) || wasSneakingOnEdge || onSlimeblock) {
//                prepareNextMove(data.getPlayer(), ticks(), sneakEdge);
//                return;
//            }
//
//            if(accelDir.lengthSquared() < 0.000001) {
//                prepareNextMove(data.getPlayer(), ticks(), sneakEdge);
//                return;
//            }
//
//            boolean vectorDir = accelDir.clone().crossProduct(yaw).dot(new Vector(0, 1, 0)) >= 0;
//            double angle = (vectorDir ? 1 : -1) *  angle(accelDir, yaw);
//
//            if(!isValidStrafe(angle)) {
//                fail("Angle: " + angle);
//            }
//
//            prepareNextMove(data.getPlayer(), ticks(), sneakEdge);
//        }
//    }
//
//    public double angle(Vector a, Vector b) {
//        double dot = Math.min(Math.max(a.dot(b) / (a.length() * b.length()), -1), 1);
//        return Math.acos(dot);
//    }
//
//    private boolean testJumped() {
//        int jumpBoostLvl = 0;
//        for (PotionEffect pEffect : data.getPlayer().getActivePotionEffects()) {
//            if (pEffect.getType().equals(PotionEffectType.JUMP)) {
//                byte amp = (byte)pEffect.getAmplifier();
//                jumpBoostLvl = amp + 1;
//                break;
//            }
//        }
//        float expectedDY = Math.max(0.42F + jumpBoostLvl * 0.1F, 0F);
//        boolean leftGround = (!data.getPositionProcessor().isOnGround() && !data.getPlayer().isOnGround());
//        double dY = data.getPositionProcessor().getDeltaY();
//
//        //Jumping right as you enter a 2-block-high space will not change your motY.
//        //When these conditions are met, we'll give them the benefit of the doubt and say that they jumped.
//        {
//            AABB box = AABB.playerCollisionBox.clone();
//            box.expand(-0.000001, -0.000001, -0.000001);
//            Location to = new Location(data.getPlayer().getWorld(), data.getPositionProcessor().getX(), data.getPositionProcessor().getY(), data.getPositionProcessor().getZ());
//            box.translate(to.toVector().add(new Vector(0, expectedDY, 0)));
//            boolean collidedNow = !box.getBlockAABBs(to.getWorld(), 8).isEmpty();
//
//            Location from = new Location(data.getPlayer().getWorld(), data.getPositionProcessor().getLastX(), data.getPositionProcessor().getLastY(), data.getPositionProcessor().getLastZ());
//
//            box = AABB.playerCollisionBox.clone();
//            box.expand(-0.000001, -0.000001, -0.000001);
//            box.translate(from.toVector().add(new Vector(0, expectedDY, 0)));
//            boolean collidedBefore = !box.getBlockAABBs(to.getWorld(), 8).isEmpty();
//
//            if(collidedNow && !collidedBefore && leftGround && dY == 0) {
//                expectedDY = 0;
//            }
//        }
//
//        Set<Material> touchedBlocks = WrappedEntity.getWrappedEntity(data.getPlayer()).getCollisionBox(new Location(data.getPlayer().getWorld(), data.getPositionProcessor().getX(), data.getPositionProcessor().getY(), data.getPositionProcessor().getZ()).toVector()).getMaterials(data.getPlayer().getWorld());
//        if(touchedBlocks.contains(Material.WEB)) {
//            expectedDY *= 0.05;
//        }
//        return ((expectedDY == 0 && data.getPositionProcessor().isOnGround()) || leftGround) && (dY == expectedDY || testTouchCeiling());
//    }
//
//    private boolean testTouchCeiling() {
//        //Change by Havesta to more accurately handle Y collision
//        AABB collisionBox = AABB.playerCollisionBox.clone();
//        collisionBox.expand(-0.000001, -0.000001, -0.000001);;
//        return checkTouchingBlock(collisionBox, data.getPlayer().getWorld(), 0.0001, 8).contains(Direction.UP);
//    }
//
//    public static Vector getDirection(float yaw, float pitch) {
//        Vector vector = new Vector();
//        float rotX = (float)Math.toRadians(yaw);
//        float rotY = (float)Math.toRadians(pitch);
//        vector.setY(-net.minecraft.server.v1_8_R3.MathHelper.sin(rotY));
//        double xz = net.minecraft.server.v1_8_R3.MathHelper.cos(rotY);
//        vector.setX(-xz * net.minecraft.server.v1_8_R3.MathHelper.sin(rotX));
//        vector.setZ(xz * net.minecraft.server.v1_8_R3.MathHelper.cos(rotX));
//        return vector;
//    }
//
//
//}
