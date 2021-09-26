

package me.tecnio.antihaxerman.api.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public final class AHMPunishEvent extends Event {

    private final Player player;

    private final String check, type;
    private final ArrayList<String> banCommands;
    private final int currentViolationLevel;
    private final double currentBuffer;

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
