package me.tecnio.ahm.check.api;

import lombok.Data;

@Data
public final class Buffer {

    private final double max;

    private double buffer;

    public double increase() {
        return increaseBy(1.0);
    }

    public double increaseBy(final double amount) {
        return this.buffer = Math.min(this.max, this.buffer + amount);
    }

    public double decrease() {
        return decreaseBy(1);
    }

    public double decreaseBy(final double amount) {
        return this.buffer = Math.max(0.0D, this.buffer - amount);
    }

    public void reset() {
        this.buffer = 0;
    }

    public void set(final double amount) {
        this.buffer = amount;
    }

    public void multiply(final double multiplier) {
        this.buffer *= multiplier;
    }
}