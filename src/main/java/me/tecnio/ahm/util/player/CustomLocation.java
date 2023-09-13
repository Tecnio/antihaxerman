package me.tecnio.ahm.util.player;

import lombok.Getter;
import org.bukkit.util.Vector;

@Getter
public class CustomLocation {

    private final double x, y, z;
    private final Vector vector;

    public CustomLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vector = new Vector(x, y, z);
    }

    public double distance(Vector otherVector) {
        return vector.distance(otherVector);
    }

}
