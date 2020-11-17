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

package me.tecnio.antihaxerman.utils.other;

import me.tecnio.antihaxerman.utils.reflection.ReflectionUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public final class BoundingBox {

    public float minX, minY, minZ, maxX, maxY, maxZ;

    public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public BoundingBox(Vector min, Vector max) {
        this.minX = (float) Math.min(min.getX(), max.getX());
        this.minY = (float) Math.min(min.getY(), max.getY());
        this.minZ = (float) Math.min(min.getZ(), max.getZ());
        this.maxX = (float) Math.max(min.getX(), max.getX());
        this.maxY = (float) Math.max(min.getY(), max.getY());
        this.maxZ = (float) Math.max(min.getZ(), max.getZ());
    }

    public BoundingBox(BoundingBox one, BoundingBox two) {
        this.minX = Math.min(one.minX, two.minX);
        this.minY = Math.min(one.minY, two.minY);
        this.minZ = Math.min(one.minZ, two.minZ);
        this.maxX = Math.max(one.maxX, two.maxX);
        this.maxY = Math.max(one.maxY, two.maxY);
        this.maxZ = Math.max(one.maxZ, two.maxZ);
    }

    public BoundingBox(final Player player) {
        this.minX = (float) (player.getLocation().getX() - 0.3);
        this.minY = (float) player.getLocation().getY();
        this.minZ = (float) (player.getLocation().getZ() - 0.3);
        this.maxX = (float) (player.getLocation().getX() + 0.3);
        this.maxY = (float) (player.getLocation().getY() + 1.8);
        this.maxZ = (float) (player.getLocation().getZ() + 0.3);
    }

    public BoundingBox add(float x, float y, float z) {
        float newMinX = minX + x;
        float newMaxX = maxX + x;
        float newMinY = minY + y;
        float newMaxY = maxY + y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox add(Vector vector) {
        float x = (float) vector.getX(), y = (float) vector.getY(), z = (float) vector.getZ();

        float newMinX = minX + x;
        float newMaxX = maxX + x;
        float newMinY = minY + y;
        float newMaxY = maxY + y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox grow(float x, float y, float z) {
        float newMinX = minX - x;
        float newMaxX = maxX + x;
        float newMinY = minY - y;
        float newMaxY = maxY + y;
        float newMinZ = minZ - z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox shrink(float x, float y, float z) {
        float newMinX = minX + x;
        float newMaxX = maxX - x;
        float newMinY = minY + y;
        float newMaxY = maxY - y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ - z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox add(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new BoundingBox(this.minX + minX, this.minY + minY, this.minZ + minZ, this.maxX + maxX, this.maxY + maxY, this.maxZ + maxZ);
    }

    public BoundingBox subtract(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new BoundingBox(this.minX - minX, this.minY - minY, this.minZ - minZ, this.maxX - maxX, this.maxY - maxY, this.maxZ - maxZ);
    }

    public boolean intersectsWithBox(Vector vector) {
        return (vector.getX() > this.minX && vector.getX() < this.maxX) && ((vector.getY() > this.minY && vector.getY() < this.maxY) && (vector.getZ() > this.minZ && vector.getZ() < this.maxZ));
    }

    public Vector getMinimum() {
        return new Vector(minX, minY, minZ);
    }

    public Vector getMaximum() {
        return new Vector(maxX, maxY, maxZ);
    }

    public List<Block> getAllBlocks(Player player) {
        Location min = new Location(player.getWorld(), Math.floor(minX), Math.floor(minY), Math.floor(minZ));
        Location max = new Location(player.getWorld(), Math.floor(maxX), Math.floor(maxY), Math.floor(maxZ));
        List<Block> all = new ArrayList<>();
        for (float x = (float) min.getX(); x < max.getX(); x++) {
            for (float y = (float) min.getY(); y < max.getY(); y++) {
                for (float z = (float) min.getZ(); z < max.getZ(); z++) {
                    Block block = new Location(player.getWorld(), x, y, z).getBlock();
                    if (block != null && block.getType().getId() != 0) {
                        all.add(block);
                    }
                }
            }
        }
        return all;
    }

    public boolean intersectsWithBox(Object other) {
        if (other instanceof BoundingBox) {
            BoundingBox otherBox = (BoundingBox) other;
            return otherBox.maxX > this.minX && otherBox.minX < this.maxX && otherBox.maxY > this.minY && otherBox.minY < this.maxY && otherBox.maxZ > this.minZ && otherBox.minZ < this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX > minX && otherMinX < maxX && otherMaxY > minY && otherMinY < maxY && otherMaxZ > minZ && otherMinZ < maxZ;
        }
    }

    public boolean collides(Vector vector) {
        return (vector.getX() >= this.minX && vector.getX() <= this.maxX) && ((vector.getY() >= this.minY && vector.getY() <= this.maxY) && (vector.getZ() >= this.minZ && vector.getZ() <= this.maxZ));
    }

    public boolean collides(Object other) {
        if (other instanceof BoundingBox) {
            BoundingBox otherBox = (BoundingBox) other;
            return otherBox.maxX >= this.minX && otherBox.minX <= this.maxX && otherBox.maxY >= this.minY && otherBox.minY <= this.maxY && otherBox.maxZ >= this.minZ && otherBox.minZ <= this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX >= minX && otherMinX <= maxX && otherMaxY >= minY && otherMinY <= maxY && otherMaxZ >= minZ && otherMinZ <= maxZ;
        }
    }

    public boolean collidesHorizontally(Vector vector) {
        return (vector.getX() >= this.minX && vector.getX() <= this.maxX) && ((vector.getY() > this.minY && vector.getY() < this.maxY) && (vector.getZ() >= this.minZ && vector.getZ() <= this.maxZ));
    }

    public boolean collidesHorizontally(Object other) {
        if (other instanceof BoundingBox) {
            BoundingBox otherBox = (BoundingBox) other;
            return otherBox.maxX >= this.minX && otherBox.minX <= this.maxX && otherBox.maxY > this.minY && otherBox.minY < this.maxY && otherBox.maxZ >= this.minZ && otherBox.minZ <= this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX >= minX && otherMinX <= maxX && otherMaxY > minY && otherMinY < maxY && otherMaxZ >= minZ && otherMinZ <= maxZ;
        }
    }

    public boolean collidesVertically(Vector vector) {
        return (vector.getX() > this.minX && vector.getX() < this.maxX) && ((vector.getY() >= this.minY && vector.getY() <= this.maxY) && (vector.getZ() > this.minZ && vector.getZ() < this.maxZ));
    }

    public boolean collidesVertically(Object other) {
        if (other instanceof BoundingBox) {
            BoundingBox otherBox = (BoundingBox) other;
            return otherBox.maxX > this.minX && otherBox.minX < this.maxX && otherBox.maxY >= this.minY && otherBox.minY <= this.maxY && otherBox.maxZ > this.minZ && otherBox.minZ < this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionUtils.getFieldValue(ReflectionUtils.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX > minX && otherMinX < maxX && otherMaxY >= minY && otherMinY <= maxY && otherMaxZ > minZ && otherMinZ < maxZ;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateXOffset(BoundingBox other, double offsetX) {
        if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (offsetX > 0.0D && other.maxX <= this.minX) {
                double d1 = this.minX - other.maxX;

                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0D && other.minX >= this.maxX) {
                double d0 = this.maxX - other.minX;

                if (d0 > offsetX) {
                    offsetX = d0;
                }
            }

            return offsetX;
        } else {
            return offsetX;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateYOffset(BoundingBox other, double offsetY) {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (offsetY > 0.0D && other.maxY <= this.minY) {
                double d1 = this.minY - other.maxY;

                if (d1 < offsetY) {
                    offsetY = d1;
                }
            } else if (offsetY < 0.0D && other.minY >= this.maxY) {
                double d0 = this.maxY - other.minY;

                if (d0 > offsetY) {
                    offsetY = d0;
                }
            }

            return offsetY;
        } else {
            return offsetY;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateZOffset(BoundingBox other, double offsetZ) {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY) {
            if (offsetZ > 0.0D && other.maxZ <= this.minZ) {
                double d1 = this.minZ - other.maxZ;

                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0D && other.minZ >= this.maxZ) {
                double d0 = this.maxZ - other.minZ;

                if (d0 > offsetZ) {
                    offsetZ = d0;
                }
            }

            return offsetZ;
        } else {
            return offsetZ;
        }
    }

    public BoundingBox addCoord(float x, float y, float z) {
        float d0 = this.minX;
        float d1 = this.minY;
        float d2 = this.minZ;
        float d3 = this.maxX;
        float d4 = this.maxY;
        float d5 = this.maxZ;

        if (x < 0.0D) {
            d0 += x;
        } else if (x > 0.0D) {
            d3 += x;
        }

        if (y < 0.0D) {
            d1 += y;
        } else if (y > 0.0D) {
            d4 += y;
        }

        if (z < 0.0D) {
            d2 += z;
        } else if (z > 0.0D) {
            d5 += z;
        }

        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    public boolean isVecInside(Vector vec) {
        return vec.getX() > this.minX && vec.getX() < this.maxX && (vec.getY() > this.minY && vec.getY() < this.maxY && vec.getZ() > this.minZ && vec.getZ() < this.maxZ);
    }

    private boolean isVecInYZ(Vector vec) {
        return vec != null && vec.getY() >= this.minY && vec.getY() <= this.maxY && vec.getZ() >= this.minZ && vec.getZ() <= this.maxZ;
    }

    private boolean isVecInXZ(Vector vec) {
        return vec != null && vec.getX() >= this.minX && vec.getX() <= this.maxX && vec.getZ() >= this.minZ && vec.getZ() <= this.maxZ;
    }

    private boolean isVecInXY(Vector vec) {
        return vec != null && vec.getX() >= this.minX && vec.getX() <= this.maxX && vec.getY() >= this.minY && vec.getY() <= this.maxY;
    }

    public String toString() {
        return "[" + minX + ", " + minY + ", " + minZ + ", " + maxX + ", " + maxY + ", " + maxZ + "]";
    }
}
