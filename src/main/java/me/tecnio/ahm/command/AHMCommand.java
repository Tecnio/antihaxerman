package me.tecnio.ahm.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import lombok.Getter;
import me.tecnio.ahm.util.string.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class AHMCommand extends BaseCommand {

    public void sendLineBreak(final CommandSender sender) {
        sender.sendMessage(ChatUtil.translate("&7&m-------------------------------------------------"));
    }

    public void sendLineBreak(final Player sender) {
        sender.sendMessage(ChatUtil.translate("&7&m-------------------------------------------------"));
    }

    public void sendMessage(final CommandSender sender, final String message) {
        sender.sendMessage(ChatUtil.translate(message));
    }

    public void sendMessage(final Player sender, final String message) {
        sender.sendMessage(ChatUtil.translate(message));
    }

    @Override
    public List<String> tabComplete(final CommandIssuer issuer, final String commandLabel, final String[] args, final boolean isAsync) throws IllegalArgumentException {
        return super.tabComplete(issuer, commandLabel, args, isAsync);
    }
}
