package me.tecnio.antihaxerman.utils.data;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class Tuple<A, B> {
    public A one;
    public B two;

    public Tuple(A one, B two) {
        this.one = one;
        this.two = two;
    }
}