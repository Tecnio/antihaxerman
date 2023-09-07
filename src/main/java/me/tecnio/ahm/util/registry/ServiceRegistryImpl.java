package me.tecnio.ahm.util.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRegistryImpl implements ServiceRegistry {
    private final Map<ServiceKey<?>, Provider<?>> registry = new ConcurrentHashMap();

    public ServiceRegistryImpl() {
    }

    @Nonnull
    public Set<ServiceKey<?>> keySet() {
        return registry.keySet();
    }

    @Nonnull
    public Set<Map.Entry<ServiceKey<?>, Provider<?>>> entrySet() {
        return registry.entrySet();
    }

    @Nullable
    public <T> T getOrNull(@Nonnull final ServiceKey<T> key) {
        final Provider<T> provider = (Provider<T>) registry.get(key);
        return provider == null ? null : provider.get();
    }

    @Nullable
    public <T> T put(@Nonnull final ServiceKey<T> key, final T service) {
        return put(key, singleton(service));
    }

    @Nullable
    public <T> T put(@Nonnull final ServiceKey<T> key, final Provider<T> service) {
        return (T) registry.put(key, service);
    }

    @Nullable
    public <T> T putIfAbsent(@Nonnull final ServiceKey<T> type, final T service) {
        return putIfAbsent(type, singleton(service));
    }

    @Nullable
    public <T> T putIfAbsent(@Nonnull final ServiceKey<T> key, final Provider<T> service) {
        return (T) registry.putIfAbsent(key, service);
    }

    private static <T> Provider<T> singleton(final T service) {
        return () -> service;
    }
}