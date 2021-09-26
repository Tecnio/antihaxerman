package me.tecnio.antihaxerman.util.type;

import me.tecnio.antihaxerman.util.ServerUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.*;

public class AABB implements Cloneable {

    public static final AABB playerCollisionBox = new AABB(new Vector(-0.3, 0, -0.3), new Vector(0.3, 1.8, 0.3));

    @Getter
    private Vector min;
    @Getter
    private Vector max;

    public AABB(Vector min, Vector max) {
        this.min = min;
        this.max = max;
    }

    public List<Block> getBlocks(World world) {
        List<Block> blocks = new ArrayList<>();
        for (int x = (int)Math.floor(min.getX()); x < (int)Math.ceil(max.getX()); x++) {
            for (int y = (int)Math.floor(min.getY()); y < (int)Math.ceil(max.getY()); y++) {
                for (int z = (int)Math.floor(min.getZ()); z < (int)Math.ceil(max.getZ()); z++) {
                    Block block = ServerUtil.getBlockAsync(new Location(world, x, y, z));

                    if(block == null)
                        continue;

                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public Set<Material> getMaterials(World world) {
        Set<Material> mats = EnumSet.noneOf(Material.class);
        for (int x = (int)Math.floor(min.getX()); x < (int)Math.ceil(max.getX()); x++) {
            for (int y = (int)Math.floor(min.getY()); y < (int)Math.ceil(max.getY()); y++) {
                for (int z = (int)Math.floor(min.getZ()); z < (int)Math.ceil(max.getZ()); z++) {
                    Block block = ServerUtil.getBlockAsync(new Location(world, x, y, z));

                    if(block == null)
                        continue;

                    mats.add(block.getType());
                }
            }
        }
        return mats;
    }

    public List<AABB> getBlockAABBs(World world, int gameVersion, Material... exemptedMats) {
        Set<Material> exempt = new HashSet<>(Arrays.asList(exemptedMats));
        List<AABB> aabbs = new ArrayList<>();

        //gotta do this to catch fences and cobble walls
        AABB expanded = this.clone();
        expanded.getMin().setY(expanded.getMin().getY() - 1);
        List<Block> blocks = expanded.getBlocks(world);

        for(Block b : blocks) {
            if(exempt.contains(b.getType()))
                continue;
            AABB[] bAABBs = WrappedBlock.getWrappedBlock(b, gameVersion).getCollisionBoxes();
            for(AABB aabb : bAABBs) {
                if(this.isColliding(aabb)) {
                    aabbs.add(aabb);
                }
            }
        }
        return aabbs;
    }

    public void expand(double x, double y, double z) {
        Vector compliment = new Vector(x, y, z);
        min.subtract(compliment);
        max.add(compliment);
    }

    public void translate(Vector vector) {
        min.add(vector);
        max.add(vector);
    }

    public AABB clone() {
        AABB clone;
        try {
            clone = (AABB) super.clone();
            clone.min = this.min.clone();
            clone.max = this.max.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isColliding(AABB other) {
        if (max.getX() < other.getMin().getX() || min.getX() > other.getMax().getX()) {
            return false;
        }
        if (max.getY() < other.getMin().getY() || min.getY() > other.getMax().getY()) {
            return false;
        }
        return !(max.getZ() < other.getMin().getZ()) && !(min.getZ() > other.getMax().getZ());
    }

    public Vector intersectsRay(RayTrace ray, float minDist, float maxDist) {
        Vector invDir = new Vector(1f / ray.direction.getX(), 1f / ray.direction.getY(), 1f / ray.direction.getZ());

        boolean signDirX = invDir.getX() < 0;
        boolean signDirY = invDir.getY() < 0;
        boolean signDirZ = invDir.getZ() < 0;

        Vector bbox = signDirX ? max : min;
        double tmin = (bbox.getX() - ray.origin.getX()) * invDir.getX();
        bbox = signDirX ? min : max;
        double tmax = (bbox.getX() - ray.origin.getX()) * invDir.getX();
        bbox = signDirY ? max : min;
        double tymin = (bbox.getY() - ray.origin.getY()) * invDir.getY();
        bbox = signDirY ? min : max;
        double tymax = (bbox.getY() - ray.origin.getY()) * invDir.getY();

        if ((tmin > tymax) || (tymin > tmax)) {
            return null;
        }
        if (tymin > tmin) {
            tmin = tymin;
        }
        if (tymax < tmax) {
            tmax = tymax;
        }

        bbox = signDirZ ? max : min;
        double tzmin = (bbox.getZ() - ray.origin.getZ()) * invDir.getZ();
        bbox = signDirZ ? min : max;
        double tzmax = (bbox.getZ() - ray.origin.getZ()) * invDir.getZ();

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return null;
        }
        if (tzmin > tmin) {
            tmin = tzmin;
        }
        if (tzmax < tmax) {
            tmax = tzmax;
        }
        if ((tmin < maxDist) && (tmax > minDist)) {
            return ray.getPointAtDistance(tmin);
        }
        return null;
    }

    public boolean betweenRays(Vector pos, Vector dir1, Vector dir2) {
        if(dir1.dot(dir2) > 0.999) {
            return this.intersectsRay(new RayTrace(pos, dir2), 0, Float.MAX_VALUE) != null;
        }
        else {
            Vector planeNormal = dir2.clone().crossProduct(dir1);
            Vector[] vertices = this.getVertices();
            boolean hitPlane = false;
            boolean above = false;
            boolean below = false;
            for(Vector vertex : vertices) {
                vertex.subtract(pos);

                if(!hitPlane) {
                    if (vertex.dot(planeNormal) > 0) {
                        above = true;
                    } else {
                        below = true;
                    }
                    if (above && below) {
                        hitPlane = true;
                    }
                }
            }
            if(!hitPlane) {
                return false;
            }

            Vector extraDirToDirNormal = planeNormal.clone().crossProduct(dir2);
            Vector dirToExtraDirNormal = dir1.clone().crossProduct(planeNormal);
            boolean betweenVectors = false;
            boolean frontOfExtraDirToDir = false;
            boolean frontOfDirToExtraDir = false;
            for(Vector vertex : vertices) {
                if(!frontOfExtraDirToDir && vertex.dot(extraDirToDirNormal) >= 0) {
                    frontOfExtraDirToDir = true;
                }
                if(!frontOfDirToExtraDir && vertex.dot(dirToExtraDirNormal) >= 0) {
                    frontOfDirToExtraDir = true;
                }

                if(frontOfExtraDirToDir && frontOfDirToExtraDir) {
                    betweenVectors = true;
                    break;
                }
            }
            return betweenVectors;
        }
    }

    public Vector[] getVertices() {
        return new Vector[]{new Vector(min.getX(), min.getY(), min.getZ()),
                new Vector(min.getX(), min.getY(), max.getZ()),
                new Vector(min.getX(), max.getY(), min.getZ()),
                new Vector(min.getX(), max.getY(), max.getZ()),
                new Vector(max.getX(), min.getY(), min.getZ()),
                new Vector(max.getX(), min.getY(), max.getZ()),
                new Vector(max.getX(), max.getY(), min.getZ()),
                new Vector(max.getX(), max.getY(), max.getZ())};
    }
}
