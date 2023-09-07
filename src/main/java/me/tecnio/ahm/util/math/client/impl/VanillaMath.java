package me.tecnio.ahm.util.math.client.impl;

import me.tecnio.ahm.util.math.client.ClientMath;
import me.tecnio.ahm.util.mcp.MathHelper;

public class VanillaMath implements ClientMath {
    @Override
    public float sin(float value) {
        return MathHelper.sin(value);
    }

    @Override
    public float cos(float value) {
        return MathHelper.cos(value);
    }

    public static float sqrt(float f) {
        return (float) Math.sqrt(f);
    }
}