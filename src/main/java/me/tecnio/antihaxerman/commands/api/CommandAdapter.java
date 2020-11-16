package me.tecnio.antihaxerman.commands.api;

import me.tecnio.antihaxerman.utils.other.ChatUtils;
import org.bukkit.entity.Player;

public abstract class CommandAdapter {

    public abstract boolean onCommand(Player player, UserInput input);

    protected void sendMessage(Player player, String input) {
        player.sendMessage(ChatUtils.color("&cAntiHaxerman &8> " + input));
    }

    protected void sendLineBreak(Player player) {
        player.sendMessage(ChatUtils.color("&c------------------------------------------------"));
    }
}
