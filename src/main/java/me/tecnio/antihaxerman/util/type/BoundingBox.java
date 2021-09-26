

package me.tecnio.antihaxerman.util.type;

import me.tecnio.antihaxerman.data.PlayerData;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class BoundingBox {

    private double minX, minY, minZ;
    private double maxX, maxY, maxZ;

    public BoundingBox(final double minX, final double maxX, final double minY, final double maxY, final double minZ, final double maxZ) {
        if (minX < maxX) {
            this.minX = minX;
            this.maxX = maxX;
        } else {
            this.minX = maxX;
            this.maxX = minX;
        }
        if (minY < maxY) {
            this.minY = minY;
            this.maxY = maxY;
        } else {
            this.minY = maxY;
            this.maxY = minY;
        }
        if (minZ < maxZ) {
            this.minZ = minZ;
            this.maxZ = maxZ;
        } else {
            this.minZ = maxZ;
            this.maxZ = minZ;
        }
    }

    public BoundingBox(final Vector data) {
        this.minX = data.getX() - 0.4;
        this.minY = data.getY();
        this.minZ = data.getZ() - 0.4;
        this.maxX = data.getX() + 0.4;
        this.maxY = data.getY() + 1.9;
        this.maxZ = data.getZ() + 0.4;
    }

    public double collidesD(RayTrace ray, double tmin, double tmax) {
        for (int i = 0; i < 3; i++) {
            double d = 1 / ray.direction(i);
            double t0 = (min(i) - ray.origin(i)) * d;
            double t1 = (max(i) - ray.origin(i)) * d;
            if (d < 0) {
                double t = t0;
                t0 = t1;
                t1 = t;
            }
            tmin = Math.max(t0, tmin);
            tmax = Math.min(t1, tmax);
            if (tmax <= tmin) return 10;
        }
        return tmin;
    }


    public BoundingBox(final PlayerData data) {
        this.minX = data.getPositionProcessor().getX() - 0.3D;
        this.minY = data.getPositionProcessor().getY();
        this.minZ = data.getPositionProcessor().getZ() - 0.3D;
        this.maxX = data.getPositionProcessor().getX() + 0.3D;
        this.maxY = data.getPositionProcessor().getY() + 1.8D;
        this.maxZ = data.getPositionProcessor().getZ() + 0.3D;
    }


    public BoundingBox(Vector vector, Vector vector1) {
        this.minX = vector.getX();
        this.minY = vector.getY();
        this.minX = vector.getX();
        this.maxX = vector1.getX();
        this.maxY = vector1.getY();
        this.maxZ = vector1.getZ();
    }

    public double min(int i) {
        switch (i) {
            case 0:
                return minX;
            case 1:
                return minY;
            case 2:
                return minZ;
            default:
                return 0;
        }
    }

    public double max(int i) {
        switch (i) {
            case 0:
                return maxX;
            case 1:
                return maxY;
            case 2:
                return maxZ;
            default:
                return 0;
        }
    }

    public AxisAlignedBB getAABB() {
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public BoundingBox move(final double x, final double y, final double z) {
        this.minX += x;
        this.minY += y;
        this.minZ += z;

        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    public void setMin(int kek, int i) {
        switch (kek) {
            case 0:
                minX = i;
            case 1:
                minY = i;
            case 2:
                minZ = i;
            default:
                move(i, i, i);
        }
    }

    public void setMax(int kek, int i) {
        switch (kek) {
            case 0:
                maxX = i;
            case 1:
                maxY = i;
            case 2:
                maxZ = i;
            default:
                move(i, i, i);
        }
    }
    public List<Block> getBlocks(final World world) {
        final List<Block> blockList = new ArrayList<>();

        final double minX = this.minX;
        final double minY = this.minY;
        final double minZ = this.minZ;
        final double maxX = this.maxX;
        final double maxY = this.maxY;
        final double maxZ = this.maxZ;

        for (double x = minX; x <= maxX; x += (maxX - minX)) {
            for (double y = minY; y <= maxY; y += (maxY - minY)) {
                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                    final Block block = world.getBlockAt(new Location(world, x, y, z));
                    blockList.add(block);
                }
            }
        }
        return blockList;
    }

    public BoundingBox expand(final double x, final double y, final double z) {
        this.minX -= x;
        this.minY -= y;
        this.minZ -= z;

        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        return this;
    }

    public BoundingBox union(final BoundingBox other) {
        final double minX = Math.min(this.minX, other.minX);
        final double minY = Math.min(this.minY, other.minY);
        final double minZ = Math.min(this.minZ, other.minZ);
        final double maxX = Math.max(this.maxX, other.maxX);
        final double maxY = Math.max(this.maxY, other.maxY);
        final double maxZ = Math.max(this.maxZ, other.maxZ);
        return new BoundingBox(minX, maxX, minY, maxY, minZ, maxZ);
    }

    public double getSize() {
        final Vector min = new Vector(this.minX, this.minY, this.minZ);
        final Vector max = new Vector(this.maxX, this.maxY, this.maxZ);
        return min.distance(max);
    }

    public BoundingBox expandSpecific(final double minX, final double maxX, final double minY, final double maxY, final double minZ, final double maxZ) {
        this.minX -= minX;
        this.minY -= minY;
        this.minZ -= minZ;

        this.maxX += maxX;
        this.maxY += maxY;
        this.maxZ += maxZ;

        return this;
    }
}
