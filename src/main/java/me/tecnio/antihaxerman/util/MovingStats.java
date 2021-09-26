

package me.tecnio.antihaxerman.util;

import java.io.Serializable;


public final class MovingStats implements Serializable {


    private final double[] elements;


    private int currentElement;
    private int windowCount;

    private double variance;

    public MovingStats(final int size) {
        this.elements = new double[size];
        this.variance = size * 2.5;


        for (int i = 0, len = this.elements.length; i < len; i++) {
            this.elements[i] = size * 2.5 / size;
        }
    }

    public void add(double sum) {
        sum /= this.elements.length;

        this.variance -= this.elements[currentElement];
        this.variance += sum;


        this.elements[currentElement] = sum;


        this.currentElement = (currentElement + 1) % this.elements.length;
    }

    public double getStdDev(final double required) {
        final double stdDev = Math.sqrt(variance);


        if (stdDev < required) {

            if (++windowCount > this.elements.length) {
                return stdDev;
            }
        } else {

            if (windowCount > 0) {
                windowCount = 0;
            }

            return required;
        }


        return Double.NaN;
    }
}
