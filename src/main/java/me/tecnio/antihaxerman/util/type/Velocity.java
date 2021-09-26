

package me.tecnio.antihaxerman.util.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public final class Velocity {
    private int index;
    private double velocityX, velocityY, velocityZ;
}
