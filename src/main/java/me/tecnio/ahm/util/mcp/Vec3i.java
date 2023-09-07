package me.tecnio.ahm.util.mcp;

public class Vec3i implements Comparable {
    /**
     * The Null vector constant (0, 0, 0)
     */
    public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);

    /**
     * X coordinate
     */
    private final int x;

    /**
     * Y coordinate
     */
    private final int y;

    /**
     * Z coordinate
     */
    private final int z;
    // private static final String __OBFID = "CL_00002315";

    public Vec3i(int p_i46007_1_, int p_i46007_2_, int p_i46007_3_) {
        this.x = p_i46007_1_;
        this.y = p_i46007_2_;
        this.z = p_i46007_3_;
    }

    public Vec3i(double p_i46008_1_, double p_i46008_3_, double p_i46008_5_) {
        this(MathHelper.floor_double(p_i46008_1_), MathHelper.floor_double(p_i46008_3_), MathHelper.floor_double(p_i46008_5_));
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof Vec3i)) {
            return false;
        } else {
            Vec3i var2 = (Vec3i) p_equals_1_;
            return this.getX() != var2.getX() ? false : (this.getY() != var2.getY() ? false : this.getZ() == var2.getZ());
        }
    }

    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    public int compareTo(Vec3i p_177953_1_) {
        return this.getY() == p_177953_1_.getY() ? (this.getZ() == p_177953_1_.getZ() ? this.getX() - p_177953_1_.getX() : this.getZ() - p_177953_1_.getZ()) : this.getY() - p_177953_1_.getY();
    }

    /**
     * Get the X coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Get the Y coordinate
     */
    public int getY() {
        return this.y;
    }

    /**
     * Get the Z coordinate
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    public Vec3i crossProduct(Vec3i vec) {
        return new Vec3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
    }

    /**
     * Calculate squared distance to the given coordinates
     *
     * @param toX X Coordinate
     * @param toY Y Coordinate
     * @param toZ Z Coordinate
     */
    public double distanceSq(double toX, double toY, double toZ) {
        double var7 = (double) this.getX() - toX;
        double var9 = (double) this.getY() - toY;
        double var11 = (double) this.getZ() - toZ;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    /**
     * Compute square of distance from point x, y, z to center of this Block
     */
    public double distanceSqToCenter(double p_177957_1_, double p_177957_3_, double p_177957_5_) {
        double var7 = (double) this.getX() + 0.5D - p_177957_1_;
        double var9 = (double) this.getY() + 0.5D - p_177957_3_;
        double var11 = (double) this.getZ() + 0.5D - p_177957_5_;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    /**
     * Calculate squared distance to the given Vector
     */
    public double distanceSq(Vec3i to) {
        return this.distanceSq((double) to.getX(), (double) to.getY(), (double) to.getZ());
    }

    public int compareTo(Object p_compareTo_1_) {
        return this.compareTo((Vec3i) p_compareTo_1_);
    }
}
