package me.tecnio.ahm.check.api.annotations;

import me.tecnio.ahm.check.api.enums.CheckState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckManifest {
    String name();

    String type();

    String description();

    CheckState state() default CheckState.STABLE;

    // Whilst -1 it will not decay automatically.
    int decay() default 60;

    int threshold() default 20;

    int maxBuffer() default Integer.MAX_VALUE;
}