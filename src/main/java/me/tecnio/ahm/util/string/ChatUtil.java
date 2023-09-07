package me.tecnio.ahm.util.string;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public final class ChatUtil {

    public String translate(final String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public String format(final String... strings) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < strings.length; i++) {
            builder.append(translate(strings[i]));

            if (i != (strings.length - 1)) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }
}
