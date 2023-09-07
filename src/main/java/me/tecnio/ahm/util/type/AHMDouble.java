package me.tecnio.ahm.util.type;

public final class AHMDouble {

    private double alpha;

    public AHMDouble() {
    }

    public AHMDouble(final double alpha) {
        this.alpha = alpha;
    }

    public double get() {
        return alpha;
    }

    public double set(final double beta) {
        return (alpha = beta);
    }

    public double add(final double beta) {
        return (this.alpha += beta);
    }

    public double subtract(final double beta) {
        return (this.alpha -= beta);
    }

    public double multiply(final double beta) {
        return (this.alpha *= beta);
    }

    public double divide(final double beta) {
        return (this.alpha /= beta);
    }
}

