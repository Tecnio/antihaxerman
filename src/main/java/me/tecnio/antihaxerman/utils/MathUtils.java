package me.tecnio.antihaxerman.utils;

import java.util.List;

public final class MathUtils {

    public long GCD_OFFSET;
    public static final double EXPANDER = Math.pow(2, 24);

    public static long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    public static double getStandardDeviation(long[] numberArray) {
        double sum = 0.0, deviation = 0.0;
        int length = numberArray.length;
        for (double num : numberArray)
            sum += num;
        double mean = sum / length;
        for (double num : numberArray)
            deviation += Math.pow(num - mean, 2);

        return Math.sqrt(deviation / length);
    }

    public static double getStandardDeviation(double[] numberArray) {
        double sum = 0.0, deviation = 0.0;
        int length = numberArray.length;
        for (double num : numberArray)
            sum += num;
        double mean = sum / length;
        for (double num : numberArray)
            deviation += Math.pow(num - mean, 2);

        return Math.sqrt(deviation / length);
    }

    public static double getStandardDeviation(List<Double> numberArray) {
        double sum = 0.0, deviation = 0.0;
        int length = numberArray.size();
        for (double num : numberArray)
            sum += num;
        double mean = sum / length;
        for (double num : numberArray)
            deviation += Math.pow(num - mean, 2);

        return Math.sqrt(deviation / length);
    }

    public static float getAngleDiff(float a, float b) {
        float diff = Math.abs(a - b);
        float altDiff = b + 360 - a;
        float altAltDiff = a + 360 - b;
        if (altDiff < diff) diff = altDiff;
        if (altAltDiff < diff) diff = altAltDiff;
        return diff;
    }

    public static boolean isScientificNotation(double val) {
        return Double.toString(val).contains("E");
    }
}
