package me.tecnio.antihaxerman.utils.other;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public final class ChatUtils {
    public String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
