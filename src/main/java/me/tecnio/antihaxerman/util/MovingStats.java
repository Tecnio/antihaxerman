/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

import java.io.Serializable;

/**
 * this is mostly stolen from apache commons
 */
public final class MovingStats implements Serializable {

    //this array contains all elements we have
    private double[] elements;

    //this is the current element index
    private int currentElement;
    private int windowCount;

    private double variance;

    public MovingStats(int size) {
        this.elements = new double[size];
        this.variance = size * 2.5;

        //We need to assign the sum to the entire double array
        for (int i = 0, len = this.elements.length; i < len; i++) {
            this.elements[i] = size * 2.5 / size;
        }
    }

    public void add(double sum) {
        sum /= this.elements.length;

        this.variance -= this.elements[currentElement];
        this.variance += sum;

        //apply the sum to the current element value
        this.elements[currentElement] = sum;

        //change our element index so it doesn't idle
        this.currentElement = (currentElement + 1) % this.elements.length;
    }

    public double getStdDev(double required) {
        double stdDev = Math.sqrt(variance);

        //the standard deviation is less than the requirement
        if (stdDev < required) {
            //count it and make sure all match
            if (++windowCount > this.elements.length) {
                return stdDev;
            }
        } else {
            //the stand deviation is greater than required, reset the count
            if (windowCount > 0) {
                windowCount = 0;
            }

            return required;
        }

        //This should never happen
        return Double.NaN;
    }
}