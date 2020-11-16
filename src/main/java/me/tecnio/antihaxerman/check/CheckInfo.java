package me.tecnio.antihaxerman.check;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInfo {
    String name();
    String type();

    int maxVL() default 20;

    boolean autoBan() default true;
}