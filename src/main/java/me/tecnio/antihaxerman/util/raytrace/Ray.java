/*
 *  Copyright (C) 2020 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
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

package me.tecnio.antihaxerman.util.raytrace;

import me.tecnio.antihaxerman.util.MathUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;


// Just a ray class I made withdataseful methods I needed.

public class Ray {

    private final Vector origin;
    private final Vector direction;

    // Create a ray at the origin pointing in a direction.
    public Ray(final Vector origin, final Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    // Create a ray based on where the player is looking.
    // Origin: Player Eye Location
    // Direction: Player-looking direction
    public static Ray from(final Player player) {
        return new Ray(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
    }

    public static Ray from(final Vector origin, final Vector direction) {
        return new Ray(origin, direction);
    }

    // (Used for rotating vectors) Creates a vector in the horizontal plane (y=0) perpendicular to a vector.
    public static Vector right(final Vector vector) {
        Vector n = vector.clone();
        n = n.setY(0).normalize();
        final double x = n.getX();
        n.setX(n.getZ());
        n.setZ(-x);
        return n;
    }

    // Returns a normalized version of this me.tecnio.antihaxerman.util.raytrace.Ray with the Y component set to 0
    public Ray level() {
        return new Ray(origin, direction.setY(0).normalize());
    }

    public Vector getOrigin() {
        return origin;
    }

    public Vector getDirection() {
        return direction;
    }

    public double origin(final int i) {
        switch (i) {
            case 0:
                return origin.getX();
            case 1:
                return origin.getY();
            case 2:
                return origin.getZ();
            default:
                return 0;
        }
    }

    public double direction(final int i) {
        switch (i) {
            case 0:
                return direction.getX();
            case 1:
                return direction.getY();
            case 2:
                return direction.getZ();
            default:
                return 0;
        }
    }

    public double getClosestPointDist(final double range, final double accuracy, final Entity entity) { //Gets multiple points on the ray, then returns the point closest to the specified hitbox
        final ArrayList<Vector> points = new ArrayList<>();
        final ArrayList<Double> distances = new ArrayList<>();
        for (double i = 0; i <= range; i += accuracy) {
            points.add(getPoint(i));
        }
        final AABB aabb = new AABB(entity);
        final Vector aabbMin = aabb.getMin();
        final Vector aabbMax = aabb.getMax();
        final Vector aabbMid = aabb.getMid();
        for (Vector point : points) {
            distances.add(MathUtil.vectorDist3D(point, aabbMin));
            distances.add(MathUtil.vectorDist3D(point, aabbMax));
            distances.add(MathUtil.vectorDist3D(point, aabbMid));
        }
        // Bukkit.broadcastMessage(df.format(smallestDist) + "");
        return MathUtil.sortForMin(distances);
    }

    public ArrayList<Vector> traverse(final double blocksAway, final double accuracy) {
        final ArrayList<Vector> positions = new ArrayList<>();
        for (double d = 0; d <= blocksAway; d += accuracy) {
            positions.add(getPoint(d));
        }
        return positions;
    }

    public boolean intersectsBB(final Vector position, final Vector min, final Vector max) {
        if (position.getX() < min.getX() || position.getX() > max.getX()) {
            return false;
        } else if (position.getY() < min.getY() || position.getY() > max.getY()) {
            return false;
        } else return !(position.getZ() < min.getZ()) && !(position.getZ() > max.getZ());
    }

    // Get a point x distance away from this ray.
    // Can bedatased to get a point 2 blocks in front of a player's face.
    public Vector getPoint(final double distance) {
        final Vector dir = new Vector(direction.getX(), direction.getY(), direction.getZ());
        final Vector orig = new Vector(origin.getX(), origin.getY(), origin.getZ());
        return orig.add(dir.multiply(distance));
    }


    // Same as above, but no need to construct object.
    public static Location getPoint(final Player player, final double distance) {
        final Vector point = Ray.from(player).getPoint(distance);
        return new Location(player.getWorld(), point.getX(), point.getY(), point.getZ());
    }

    public void highlight(final World world, final double blocksAway, final double accuracy) {
        for(final Vector position: traverse(blocksAway, accuracy)) {
            world.playEffect(position.toLocation(world), Effect.SMOKE, 0);
        }
    }
}