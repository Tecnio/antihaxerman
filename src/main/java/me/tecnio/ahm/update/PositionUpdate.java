package me.tecnio.ahm.update;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class PositionUpdate {
    private final double x, y, z;
    private final double lastX, lastY, lastZ;
    private final double deltaX, deltaY, deltaZ;
    private final double lastDeltaX, lastDeltaY, lastDeltaZ;
    private final boolean onGround, lastOnGround;
    private final boolean position, look;
}
