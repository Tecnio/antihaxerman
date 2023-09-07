package me.tecnio.ahm.alert.alert;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.data.PlayerData;

import java.util.Set;

public interface AlertHandler {
    void handle(final Set<PlayerData> listeners, final String base, final Check check, final String information);
}
