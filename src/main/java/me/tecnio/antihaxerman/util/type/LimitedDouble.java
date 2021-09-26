package me.tecnio.antihaxerman.util.type;

public class LimitedDouble {
    final float min;
    final float max;
    float value;

    public LimitedDouble(final float min, final float max, final float value) {
        this.value = 0.0f;
        this.min = min;
        this.max = max;
        this.value = value;
        this.fix();
    }

    public LimitedDouble(final float n, final float max) {
        this.value = 0.0f;
        this.min = n;
        this.max = max;
        this.value = n;
    }

    public void addValue(final float n) {
        this.value += n;
        this.fix();
    }

    public void reset() {
        this.value = this.min;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(final float value) {
        this.value = value;
        this.fix();
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    void fix() {
        this.value = Math.min(this.max, Math.max(this.min, this.value));
    }
}
