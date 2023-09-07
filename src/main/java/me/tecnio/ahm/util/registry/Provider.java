package me.tecnio.ahm.util.registry;

public interface Provider<T> {
    T get();
}