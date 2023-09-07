package me.tecnio.ahm.check.api;

import lombok.Getter;
import lombok.SneakyThrows;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.Disabled;
import me.tecnio.ahm.data.PlayerData;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class CheckManager {

    private final List<Class<?>> checks = new ArrayList<>();
    private final List<Constructor<?>> constructors = new ArrayList<>();

    public CheckManager() {
        ClassIndex.getSubclasses(Check.class, Check.class.getClassLoader()).forEach(clazz -> {
            try {
                if (!Modifier.isAbstract(clazz.getModifiers()) && !clazz.isAnnotationPresent(Disabled.class)) {
                    this.checks.add(clazz);
                    this.constructors.add(clazz.getConstructor(PlayerData.class));
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @SneakyThrows
    public List<Check> loadChecks(final PlayerData data) {
        final List<Check> checkList = new ArrayList<>();

        for (final Constructor<?> constructor : this.constructors) {
            checkList.add((Check) constructor.newInstance(data));
        }

        return checkList;
    }
}