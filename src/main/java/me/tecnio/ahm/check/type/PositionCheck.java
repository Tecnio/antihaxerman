package me.tecnio.ahm.check.type;

import me.tecnio.ahm.update.PositionUpdate;

public interface PositionCheck {
    void handle(final PositionUpdate update);
}