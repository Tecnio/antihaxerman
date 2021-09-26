

package me.tecnio.antihaxerman.util.type;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public final class Pair<X, Y> {
    private X x;
    private Y y;
}
