package me.tecnio.ahm.util.math.client.impl;

import me.tecnio.ahm.util.math.client.ClientMath;

public class OptifineMath implements ClientMath {
    private static final float[] SIN_TABLE_FAST = new float[4096];
    private static final float radToIndex = roundToFloat(651.8986469044033D);

    static {
        for (int j = 0; j < SIN_TABLE_FAST.length; ++j) {
            SIN_TABLE_FAST[j] = roundToFloat(Math.sin((double) j * Math.PI * 2.0D / 4096.0D));
        }
    }

    @Override
    public float sin(float value) {
        return SIN_TABLE_FAST[(int) (value * radToIndex) & 4095];
    }

    @Override
    public float cos(float value) {
        return SIN_TABLE_FAST[(int) (value * radToIndex + 1024.0F) & 4095];
    }

    public static float roundToFloat(double d) {
        return (float) ((double) Math.round(d * 1.0E8D) / 1.0E8D);
    }
}