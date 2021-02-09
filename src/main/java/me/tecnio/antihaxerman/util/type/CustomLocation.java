/*
 *  Copyright (C) 2020 - 2021 Tecnio
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

package me.tecnio.antihaxerman.util.type;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

@Getter
@Setter
public final class CustomLocation {

    private World world;
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
    private long timeStamp;

    public CustomLocation(final World world, final double x, final double y, final double z, final float yaw, final float pitch, final boolean onGround) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
        this.timeStamp = System.nanoTime() / 10000;
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public CustomLocation offset(final double x, final double y, final double z, final float yaw, final float pitch) {
        return new CustomLocation(world, this.x + x, this.y + y, this.z + z, this.yaw + yaw, this.pitch + pitch, this.onGround);
    }

    public CustomLocation clone() {
        return new CustomLocation(world, x, y, z, yaw, pitch, onGround);
    }

    public static CustomLocation fromBukkit(final Location location) {
        return new CustomLocation(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), false);
    }

    public Location toBukkit(){ return new Location(world, x, y, z); }

    public Vector getDirection() {
        final Vector vector = new Vector();
        final double rotX = this.getYaw();
        final double rotY = this.getPitch();
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        final double xz = Math.cos(Math.toRadians(rotY));
        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        return vector;
    }
}
