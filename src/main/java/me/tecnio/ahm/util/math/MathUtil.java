package me.tecnio.ahm.util.math;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import me.tecnio.ahm.util.type.Pair;
import net.minecraft.server.v1_8_R3.Tuple;

import java.util.*;

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

        return variance / count;
    }

    public double getMin(final Collection<? extends Number> collection) {
        double min = Double.MAX_VALUE;

        for (final Number number : collection){
            min = Math.min(min, number.doubleValue());
        }

        return min;
    }

    public double getMax(final Collection<? extends Number> collection) {
        double max = Double.MIN_VALUE;

        for (final Number number : collection) {
            max = Math.max(max, number.doubleValue());
        }

        return max;
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

    public static long getMode(final Collection<? extends Number> array) {
        long mode = (long) array.toArray()[0];
        long maxCount = 0;

        for (final Number value : array) {
            int count = 1;

            for (final Number i : array) {
                if (i.equals(value))
                    count++;

                if (count > maxCount) {
                    mode = (long) value;
                    maxCount = count;
                }
            }
        }

        return mode;
    }

    public Double getMode(final double[] collect) {
        final Map<Double, Integer> repeated = new HashMap<>();

        //Sorting each value by how to repeat into a map.
        Arrays.stream(collect).forEach(val -> {
            final int number = repeated.getOrDefault(val, 0);

            repeated.put(val, number + 1);
        });

        //Calculating the largest value to the key, which would be the mode.
        return repeated.keySet().stream()
                .map(key -> new Tuple<>(key, repeated.get(key))) //We map it into a Tuple for easier sorting.
                .max(Comparator.comparing(Tuple::b, Comparator.naturalOrder()))
                .orElseThrow(NullPointerException::new)
                .a();
    }

    public float distanceBetweenAngles(final float alpha, final float beta) {
        final float alphaX = alpha % 360;
        final float betaX = beta % 360;
        final float delta = Math.abs(alphaX - betaX);

        return (float) Math.abs(Math.min(360.0 - delta, delta));
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
        return number.doubleValue() < 1 && (Double.toString(number.doubleValue()).contains("E") || number.doubleValue() == 0.0);
    }

    public boolean isExponentiallyLarge(final Number number) {
        return number.doubleValue() > 10000 && Double.toString(number.doubleValue()).contains("E");
    }

    public long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    public double getGcd(final double a, final double b) {
        if (a < b) {
            return getGcd(b, a);
        }

        if (Math.abs(b) < 0.00001) {
            return a;
        } else {
            return getGcd(b, a - Math.floor(a / b) * b);
        }
    }

    public static long getAbsoluteGcd(final float current, final float last) {
        final long currentExpanded = (long) (current * EXPANDER);
        final long lastExpanded = (long) (last * EXPANDER);

        return getGcd(currentExpanded, lastExpanded);
    }

    public static long getAbsoluteGcd(final double current, final double last) {
        final long currentExpanded = (long) (current * EXPANDER);
        final long lastExpanded = (long) (last * EXPANDER);

        return getGcd(currentExpanded, lastExpanded);
    }

    public static float gcdRational(final List<Float> numbers) {
        float result = numbers.get(0);

        for (int i = 1; i < numbers.size(); i++) {
            result = gcdRational(numbers.get(i), result);

            if (result < 1.0E-7) { //This usually means that the GCD is beyond the precision we can handle
                return 0;
            }
        }

        return result;
    }

    public static float gcdRational(final float a, final float b) {
        if (a == 0) {
            return b;
        }

        final int quotient = getIntQuotient(b, a);

        float remainder = ((b / a) - quotient) * a;

        if (Math.abs(remainder) < Math.max(a, b) * 1.0E-3F) {
            remainder = 0;
        }

        return gcdRational(remainder, a);
    }

    public static int getIntQuotient(final float dividend, final float divisor) {
        final float ans = dividend / divisor;
        final float error = Math.max(dividend, divisor) * 1.0E-3F;

        return (int) (ans + error);
    }

    public double getCps(final Collection<? extends Number> data) {
        return 20 / getAverage(data);
    }

    public int getDuplicates(final Collection<? extends Number> data) {
        return data.size() - getDistinct(data);
    }

    /**
     * @param - The collection of numbers you want analyze
     * @return - A pair of the high and low outliers
     *
     * @See - https://en.wikipedia.org/wiki/Outlier
     */
    public Pair<List<Double>, List<Double>> getOutliers(final Collection<? extends Number> collection) {
        final List<Double> values = new ArrayList<>();

        for (final Number number : collection) {
            values.add(number.doubleValue());
        }

        final double q1 = getMedian(values.subList(0, values.size() / 2));
        final double q3 = getMedian(values.subList(values.size() / 2, values.size()));

        final double iqr = Math.abs(q1 - q3);
        final double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

        final Pair<List<Double>, List<Double>> tuple = new Pair<>(new ArrayList<>(), new ArrayList<>());

        for (final Double value : values) {
            if (value < lowThreshold) {
                tuple.getX().add(value);
            }
            else if (value > highThreshold) {
                tuple.getY().add(value);
            }
        }

        return tuple;
    }


    public int getDistinct(final Collection<? extends Number> data) {
        return (int) data.stream().distinct().count();
    }

    public double hypot(final double a, final double b) {
        return Math.sqrt(a * a + b * b);
    }
}
