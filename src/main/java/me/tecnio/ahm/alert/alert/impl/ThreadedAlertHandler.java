package me.tecnio.ahm.alert.alert.impl;

import me.tecnio.ahm.alert.alert.AlertHandler;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.enums.CheckState;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.util.string.ChatUtil;
import net.md_5.bungee.api.chat.*;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadedAlertHandler implements AlertHandler {

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public void handle(final Set<PlayerData> listeners, final String base, final Check check, final String information) {
        this.executor.execute(() -> {
            final PlayerData data = check.getData();

            final String description = check.getDescription();
            final String state = check.getState().getDisplayName();
            final int ping = data.getConnectionTracker().getTransactionPing();
            final int max = check.getMaxVl();

            final String message = ChatUtil.translate(base
                    .replaceAll("%player%", data.getPlayer().getName())
                    .replaceAll("%check%", check.getName())
                    .replaceAll("%dev%", check.getState() != CheckState.STABLE ? "&7*" : "")
                    .replaceAll("%vl%", Integer.toString(check.getViolations()))
                    .replaceAll("%type%", check.getType()));

            final String hover = ChatUtil.translate(String.join("\n", Arrays.asList(
                    "&c" + description,
                    " ",
                    "&7" + information,
                    " ",
                    "&cPing: &7" + ping + "ms" + " &cMaxVl: &7" + max + " &cState: &7" + state)));

            final TextComponent alertMessage = new TextComponent(message);
            final BaseComponent[] baseComponents = new ComponentBuilder(hover).create();

            alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
            alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, baseComponents));

            listeners.forEach(player -> player.getPlayer().spigot().sendMessage(alertMessage));
        });
    }
}
