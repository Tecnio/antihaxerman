package me.tecnio.antihaxerman.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Getter
@Setter
public final class CustomLocation {
    private double x, y, z;
    private float yaw, pitch;
    private long timeStamp;

    public CustomLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();

        this.timeStamp = System.currentTimeMillis();
    }

    public Vector toVector(){
        return new Vector(x, y, z);
    }
}
