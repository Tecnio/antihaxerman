

package me.tecnio.antihaxerman.config;

import lombok.Getter;
import lombok.Setter;

public final class ConfigValue {
    @Setter
    private Object value;
    @Getter
    private final ValueType type;
    @Getter
    private final String name;

    public ConfigValue(final ValueType type, final String name) {
        this.type = type;
        this.name = name;
    }

    public boolean getBoolean() {
        return (boolean) value;
    }

    public double getDouble() {
        return (double) value;
    }

    public int getInt() {
        return (int) value;
    }

    public long getLong() {
        return (long) value;
    }

    public String getString() {
        return (String) value;
    }

    public enum ValueType {
        INTEGER,
        DOUBLE,
        BOOLEAN,
        STRING,
        LONG
    }
}
