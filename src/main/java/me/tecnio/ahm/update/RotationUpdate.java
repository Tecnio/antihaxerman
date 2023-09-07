package me.tecnio.ahm.update;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class RotationUpdate {
    private final float yaw, pitch;
    private final float lastYaw, lastPitch;
    private final float deltaYaw, deltaPitch;
    private final float lastDeltaYaw, lastDeltaPitch;
    private final float yawAccel, pitchAccel;
    private final double absGcdPitch, absGcdYaw;
}
