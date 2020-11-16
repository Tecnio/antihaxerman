package me.tecnio.antihaxerman.utils.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class Pair<X, Y> {
    private X x;
    private Y y;

    public X getKey() {
        return x;
    }

    public Y getValue() {
        return y;
    }
}


