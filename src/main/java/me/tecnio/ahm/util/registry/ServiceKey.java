package me.tecnio.ahm.util.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class ServiceKey<T> {
    private final Class<T> type;
    private final int hashCode;
    private final String name;

    private ServiceKey(final Class<T> type, final String name) {
        this.type = type;
        this.name = name;
        this.hashCode = Objects.hash(type, name);
    }

    @Nonnull
    public Class<T> getType() {
        return type;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public boolean equals(final Object obj) {
        if (!(obj instanceof ServiceKey)) {
            return false;

        } else {

            final ServiceKey that = (ServiceKey)obj;
            return type == that.type && Objects.equals(name, that.name);
        }
    }

    public int hashCode() {
        return hashCode;
    }

    public String toString() {
        return name == null ? type.getName() : type.getName() + "(" + name + ")";
    }

    @Nonnull
    public static <T> ServiceKey<T> key(@Nonnull final Class<T> type) {
        return new ServiceKey(type, null);
    }

    @Nonnull
    public static <T> ServiceKey<T> key(@Nonnull final Class<T> type, @Nonnull final String name) {
        return new ServiceKey(type, name);
    }
}
