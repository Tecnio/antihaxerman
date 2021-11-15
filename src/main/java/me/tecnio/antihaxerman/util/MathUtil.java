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

package me.tecnio.antihaxerman.util;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class MathUtil {

    public final double EXPANDER = Math.pow(2, 24);

    public double getVariance(final Collection<? extends Number> data) {
        int count = 0;

        double sum = 0.0;
        double variance = 0.0;

        final double average;

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        average = sum / count;

        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }

        return variance;
    }

    public double getStandardDeviation(final Collection<? extends Number> data) {
        final double variance = getVariance(data);

        return Math.sqrt(variance);
    }

    public double getSkewness(final Collection<? extends Number> data) {
        double sum = 0;
        int count = 0;

        final List<Double> numbers = Lists.newArrayList();

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;

            numbers.add(number.doubleValue());
        }

        Collections.sort(numbers);

        final double mean = sum / count;
        final double median = (count % 2 != 0) ? numbers.get(count / 2) : (numbers.get((count - 1) / 2) + numbers.get(count / 2)) / 2;
        final double variance = getVariance(data);

        return 3 * (mean - median) / variance;
    }

    public double getAverage(final Collection<? extends Number> data) {
        double sum = 0.0;

        for (final Number number : data) {
            sum += number.doubleValue();
        }

        return sum / data.size();
    }

    public double getKurtosis(final Collection<? extends Number> data) {
        double sum = 0.0;
        int count = 0;

        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        if (count < 3.0) {
            return 0.0;
        }

        final double efficiencyFirst = count * (count + 1.0) / ((count - 1.0) * (count - 2.0) * (count - 3.0));
        final double efficiencySecond = 3.0 * Math.pow(count - 1.0, 2.0) / ((count - 2.0) * (count - 3.0));
        final double average = sum / count;

        double variance = 0.0;
        double varianceSquared = 0.0;

        for (final Number number : data) {
            variance += Math.pow(average - number.doubleValue(), 2.0);
            varianceSquared += Math.pow(average - number.doubleValue(), 4.0);
        }

        return efficiencyFirst * (varianceSquared / Math.pow(variance / sum, 2.0)) - efficiencySecond;
    }

    public static int getMode(final Collection<? extends Number> array) {
        int mode = (int) array.toArray()[0];
        int maxCount = 0;
        for (final Number value : array) {
            int count = 1;
            for (final Number i : array) {
                if (i.equals(value))
                    count++;
                if (count > maxCount) {
                    mode = (int) value;
                    maxCount = count;
                }
            }
        }
        return mode;
    }

    public double getModeDouble(final Double[] data) {
        double maxValue = -1.0d;
        int maxCount = 0;

        for (int i = 0; i < data.length; ++i) {
            final double currentValue = data[i];
            int currentCount = 1;
            for (int j = i + 1; j < data.length; ++j) {
                if (Math.abs(data[j] - currentValue) < 0.001) {
                    ++currentCount;
                }
            }
            if (currentCount > maxCount) {
                maxCount = currentCount;
                maxValue = currentValue;
            } else if (currentCount == maxCount) {
                maxValue = Double.NaN;
            }
        }

        return maxValue;
    }

    private double getMedian(final List<Double> data) {
        if (data.size() % 2 == 0) {
            return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
        } else {
            return data.get(data.size() / 2);
        }
    }

    public boolean isExponentiallySmall(final Number number) {
        return Math.abs(number.doubleValue()) < 9E-5;
    }

    public boolean isExponentiallyLarge(final Number number) {
        return Math.abs(number.doubleValue()) > 1E5;
    }

    public long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    public double getGcd(final double a, final double b) {
        if (a < b) {
            return getGcd(b, a);
        }

        if (Math.abs(b) < 0.001) {
            return a;
        } else {
            return getGcd(b, a - Math.floor(a / b) * b);
        }
    }

    public double getCps(final Collection<? extends Number> data) {
        return 20 / getAverage(data);
    }

    public int getDuplicates(final Collection<? extends Number> data) {
        return (int) (data.size() - data.stream().distinct().count());
    }

    public int getDistinct(final Collection<? extends Number> data) {
        return (int) data.stream().distinct().count();
    }

    public static float getMoveAngle(final Location to, final Location from) {
        final double diffX = to.getX() - from.getX();
        final double diffZ = to.getZ() - from.getZ();

        final float moveAngle = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F);

        return angleTo180(Math.abs(moveAngle - to.getYaw()));
    }

    public static float angleTo180(float val) {
        val = val % 360F;

        if (val >= 180F) {
            val -= 360F;
        }

        if (val < -180F) {
            val += 360F;
        }

        return val;
    }

    public static Vector getDirection(final float yaw, final float pitch) {
        final Vector vector = new Vector();
        final float rotX = (float) Math.toRadians(yaw);
        final float rotY = (float) Math.toRadians(pitch);
        vector.setY(-Math.sin(rotY));
        final double xz = Math.cos(rotY);
        vector.setX(-xz * Math.sin(rotX));
        vector.setZ(xz * Math.cos(rotX));
        return vector;
    }

    public static float[] getRotations(final Location one, final Location two) {
        final double diffX = two.getX() - one.getX();
        final double diffZ = two.getZ() - one.getZ();
        final double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        final double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static float clamp(final float val, final float min, final float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double vectorDist3D(final Vector a, final Vector b) {
        final double xSqrd = Math.pow(Math.abs(a.getX() - b.getX()), 2);
        final double ySqrd = Math.pow(Math.abs(a.getY() - b.getY()), 2);
        final double zSqrd = Math.pow(Math.abs(a.getZ() - b.getZ()), 2);

        return Math.sqrt(xSqrd + ySqrd + zSqrd);
    }

    public static double sortForMin(final ArrayList<Double> al) {
        double min = al.get(0);

        for (int i = 1; i < al.size(); i++) {
            if (al.get(i) < min) {
                min = al.get(i);
            }
        }

        return min;
    }

    public double averageTwoNum(final double a, final double b) {
        return (a + b) / 2;
    }


    public int msToTicks(final double time) {
        return (int) Math.round(time / 50.0);
    }

    public double hypot(final double... number) {
        double sum = 0.0;

        for (final double v : number) {
            sum += v * v;
        }

        return Math.sqrt(sum);
    }

    public double hypot(final double a, final double b) {
        return Math.sqrt((a * a) + (b * b));
    }
}
