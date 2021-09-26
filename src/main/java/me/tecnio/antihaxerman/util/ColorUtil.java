

package me.tecnio.antihaxerman.util;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class ColorUtil {

    public String translate(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
