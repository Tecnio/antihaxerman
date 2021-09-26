package me.tecnio.antihaxerman.util.type;

import me.tecnio.antihaxerman.util.WrappedBlockUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class WrappedBlock extends WrappedBlockUtil {

    private final net.minecraft.server.v1_8_R3.Block block;

    public WrappedBlock(Block block, int clientVersion) {
        super(block, clientVersion);
        BlockPosition.MutableBlockPosition bPos = new BlockPosition.MutableBlockPosition();
        bPos.c(block.getX(), block.getY(), block.getZ());
        IBlockData data;

        //i know this is a poor idea. you have a better idea?
        while(true) {
            try {
                data = ((CraftWorld) block.getWorld()).getHandle().getType(bPos);
                break;
            } catch (ConcurrentModificationException ignore) { }
        }

        net.minecraft.server.v1_8_R3.Block b = this.block = data.getBlock();
        b.updateShape(((CraftWorld) block.getWorld()).getHandle(), bPos);

        strength = b.g(null, null);
        solid = isReallySolid(block); //don't run hawk on torch 1.8.8! you'll get async-entity-add errors here.
        hitbox = getHitBox(b, block.getLocation());
        collisionBoxes = getCollisionBoxes(b, block.getLocation(), bPos, data);
        slipperiness = b.frictionFactor;
    }

    @Override
    public net.minecraft.server.v1_8_R3.Block getNMS() {
        return block;
    }

    @Override
    public void sendPacketToPlayer(Player p) {
        Location loc = getBukkitBlock().getLocation();
        PacketPlayOutBlockChange pac = new PacketPlayOutBlockChange(((CraftWorld) loc.getWorld()).getHandle(), new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(pac);
    }

    @Override
    public float getDamage(HumanEntity entity) {
        return block.getDamage(((CraftHumanEntity)entity).getHandle(), ((CraftWorld)obBlock.getWorld()).getHandle(), new BlockPosition(obBlock.getX(), obBlock.getY(), obBlock.getZ()));
    }

    @Override
    public boolean isMaterialAlwaysDestroyable() {
        return block.getMaterial().isAlwaysDestroyable();
    }

    private AABB getHitBox(net.minecraft.server.v1_8_R3.Block b, Location loc) {

        Vector min = new Vector(loc.getX() + b.B(), loc.getY() + b.D(), loc.getZ() + b.F());
        Vector max = new Vector(loc.getX() + b.C(), loc.getY() + b.E(), loc.getZ() + b.G());

        return new AABB(min, max);
    }

    private boolean isReallySolid(Block b) {
        boolean reallySolid = block.getMaterial().isSolid();
        MaterialData matData = b.getState().getData();
        if (matData instanceof Sign || matData instanceof Banner)
            reallySolid = false;
        else if (matData instanceof FlowerPot || matData instanceof Diode || matData instanceof Skull ||
                b.getType() == Material.CARPET || matData instanceof Ladder ||
                b.getType() == Material.REDSTONE_COMPARATOR || b.getType() == Material.REDSTONE_COMPARATOR_ON ||
                b.getType() == Material.REDSTONE_COMPARATOR_OFF || b.getType() == Material.SOIL ||
                b.getType() == Material.WATER_LILY || b.getType() == Material.SNOW || b.getType() == Material.COCOA) {
            reallySolid = true;
        }
        return reallySolid;
    }

    private AABB[] getCollisionBoxes(net.minecraft.server.v1_8_R3.Block b, Location loc, BlockPosition bPos, IBlockData data) {

        //define boxes for funny blocks
        if (b instanceof BlockCarpet) {
            AABB[] aabbarr = new AABB[1];
            if(clientVersion == 8) {
                aabbarr[0] = new AABB(loc.toVector(), loc.toVector().add(new Vector(1, 0.0625, 1)));
            }
            else {
                aabbarr[0] = new AABB(loc.toVector(), loc.toVector().add(new Vector(1, 0, 1)));
            }

            return aabbarr;
        }
        if (b instanceof BlockSnow && data.get(BlockSnow.LAYERS) == 1) {
            AABB[] aabbarr = new AABB[1];
            aabbarr[0] = new AABB(loc.toVector(), loc.toVector().add(new Vector(1, 0, 1)));
            return aabbarr;
        }

        //perhaps you would want to do this for anvils and chests, too... somehow? AABB on clientside takes the shape of the last anvil or chest you looked at.

        List<AxisAlignedBB> bbs = new ArrayList<>();
        AxisAlignedBB cube = AxisAlignedBB.a(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getBlockX() + 1, loc.getBlockY() + 1, loc.getBlockZ() + 1);
        b.a(((CraftWorld) loc.getWorld()).getHandle(), bPos, data, cube, bbs, null);

        AABB[] collisionBoxes = new AABB[bbs.size()];
        for (int i = 0; i < bbs.size(); i++) {
            AxisAlignedBB bb = bbs.get(i);
            AABB collisionBox = new AABB(new Vector(bb.a, bb.b, bb.c), new Vector(bb.d, bb.e, bb.f));
            collisionBoxes[i] = collisionBox;
        }

        return collisionBoxes;
    }

    @Override
    public Vector getFlowDirection() {

        if(!block.getMaterial().isLiquid())
            return new Vector();

        //this should prevent async threads from calling NMS code that actually loads chunks
        if(!Bukkit.isPrimaryThread()) {
            if(!obBlock.getWorld().isChunkLoaded(obBlock.getX() >> 4, obBlock.getZ() >> 4) ||
                    !obBlock.getWorld().isChunkLoaded(obBlock.getX() + 1 >> 4, obBlock.getZ() >> 4) ||
                    !obBlock.getWorld().isChunkLoaded(obBlock.getX() - 1 >> 4, obBlock.getZ() >> 4) ||
                    !obBlock.getWorld().isChunkLoaded(obBlock.getX() >> 4, obBlock.getZ() + 1 >> 4) ||
                    !obBlock.getWorld().isChunkLoaded(obBlock.getX() >> 4, obBlock.getZ() - 1 >> 4)) {
                return new Vector();
            }
        }

        Entity dummy = null;
        Vec3D nmsVec = new Vec3D(0, 0, 0);
        BlockPosition bPos = new BlockPosition(obBlock.getX(), obBlock.getY(), obBlock.getZ());
        nmsVec = block.a(((CraftWorld) obBlock.getWorld()).getHandle(), bPos, dummy, nmsVec);
        return new Vector(nmsVec.a, nmsVec.b, nmsVec.c);
    }
}
