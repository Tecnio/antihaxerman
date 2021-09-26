

package me.tecnio.antihaxerman.manager;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.ColorUtil;
import me.tecnio.antihaxerman.util.LogUtil;
import me.tecnio.antihaxerman.util.ServerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
public final class AlertManager {

    private static final Set<PlayerData> alerts = new HashSet<>();

    public static ToggleAlertType toggleAlerts(final PlayerData data) {
        if (alerts.contains(data)) {
            alerts.remove(data);
            return ToggleAlertType.REMOVE;
        } else {
            alerts.add(data);
            return ToggleAlertType.ADD;
        }
    }

    public static void handleAlert(final Check check, final PlayerData data, final String info) {

        if(check.getCheckInfo() != null) {
            if (Config.LOGGING_ENABLED) {
                final String log = Config.LOG_FORMAT.replaceAll("%player%", data.getPlayer().getName())
                        .replaceAll("%check%", check.getCheckInfo().name())
                        .replaceAll("%dev%", check.getCheckInfo().experimental() ? ColorUtil.translate("&7*") : "")
                        .replaceAll("%vl%", Integer.toString(check.getVl()))
                        .replaceAll("%type%", check.getCheckInfo().type())
                        .replaceAll("%date%", new Date().toString())
                        .replaceAll("%tps%", String.valueOf(Math.min(ServerUtil.getTPS(), 20.0)))
                        .replaceAll("%info%", info)
                        .replaceAll("%tping%", String.valueOf(data.getConnectionProcessor().getTransactionPing()))
                        .replaceAll("%kaping%", String.valueOf(data.getConnectionProcessor().getKeepAlivePing()));

                LogUtil.logToFile(data.getLogFile(), log);
            }

            final TextComponent alertMessage = new TextComponent(ColorUtil.translate(Config.ALERT_FORMAT)
                    .replaceAll("%player%", data.getPlayer().getName())
                    .replaceAll("%check%", check.getCheckInfo().name())
                    .replaceAll("%dev%", check.getCheckInfo().experimental() ? ColorUtil.translate("&7*") : "")
                    .replaceAll("%vl%", Integer.toString(check.getVl()))
                    .replaceAll("%type%", check.getCheckInfo().type())
                    .replaceAll("%maxvl%", String.valueOf(check.getMaxVl())));

            alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
            alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.translate(
                    "&cDescription: &f" + check.getCheckInfo().description() +
                            "\n&cInfo: &b" + info +
                            "\n&cPing: &b" + PacketEvents.get().getPlayerUtils().getPing(data.getPlayer()) +
                            "\n&cTransPing: &b" + data.getConnectionProcessor().getTransactionPing() +
                            "\n&cKeepAlivePing: &b" + data.getConnectionProcessor().getKeepAlivePing() +
                            "\n&cTPS: &f" + String.format("%.2f", Math.min(20, PacketEvents.get().getServerUtils().getTPS())) +
                            "\n&cClick to teleport.")).create()));

            alerts.forEach(player -> player.getPlayer().spigot().sendMessage(alertMessage));
        } else {
            if (Config.LOGGING_ENABLED) {
                if(check.custom == 1) {
                    final String log = Config.LOG_FORMAT.replaceAll("%player%", data.getPlayer().getName())
                            .replaceAll("%check%", "Speed")
                            .replaceAll("%dev%", "")
                            .replaceAll("%vl%", Integer.toString(check.getVl()))
                            .replaceAll("%type%", "F")
                            .replaceAll("%date%", new Date().toString())
                            .replaceAll("%tps%", String.valueOf(Math.min(ServerUtil.getTPS(), 20.0)))
                            .replaceAll("%info%", info)
                            .replaceAll("%tping%", String.valueOf(data.getConnectionProcessor().getTransactionPing()))
                            .replaceAll("%kaping%", String.valueOf(data.getConnectionProcessor().getKeepAlivePing()));

                    LogUtil.logToFile(data.getLogFile(), log);
                }
                else if(check.custom == 2) {
                    final String log = Config.LOG_FORMAT.replaceAll("%player%", data.getPlayer().getName())
                            .replaceAll("%check%", "FastBow")
                            .replaceAll("%dev%", "")
                            .replaceAll("%vl%", Integer.toString(check.getVl()))
                            .replaceAll("%type%", "A")
                            .replaceAll("%date%", new Date().toString())
                            .replaceAll("%tps%", String.valueOf(Math.min(ServerUtil.getTPS(), 20.0)))
                            .replaceAll("%info%", info)
                            .replaceAll("%tping%", String.valueOf(data.getConnectionProcessor().getTransactionPing()))
                            .replaceAll("%kaping%", String.valueOf(data.getConnectionProcessor().getKeepAlivePing()));

                    LogUtil.logToFile(data.getLogFile(), log);
                }
            }
            TextComponent alertMessage = new TextComponent("");
            if(check.custom == 1) {
                alertMessage = new TextComponent(ColorUtil.translate(Config.ALERT_FORMAT)
                        .replaceAll("%player%", data.getPlayer().getName())
                        .replaceAll("%check%", "Speed")
                        .replaceAll("%dev%", "")
                        .replaceAll("%vl%", Integer.toString(check.getVl()))
                        .replaceAll("%type%", "F")
                        .replaceAll("%maxvl%", String.valueOf(check.getMaxVl())));

                alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
                alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.translate(
                        "&cDescription: &f" + "Checks for ZoneCraft speed (And some other speeds too!)." +
                                "\n&cInfo: &7" + info +
                                "\n&cPing: &7" + PacketEvents.get().getPlayerUtils().getPing(data.getPlayer()) +
                                "\n&cTPS: &7" + String.format("%.2f", Math.min(20, PacketEvents.get().getServerUtils().getTPS())) +
                                "\n&cClick to teleport.")).create()));
            }
            else if(check.custom == 2) {
                alertMessage = new TextComponent(ColorUtil.translate(Config.ALERT_FORMAT)
                        .replaceAll("%player%", data.getPlayer().getName())
                        .replaceAll("%check%", "FastBow")
                        .replaceAll("%dev%", "")
                        .replaceAll("%vl%", Integer.toString(check.getVl()))
                        .replaceAll("%type%", "A")
                        .replaceAll("%maxvl%", String.valueOf(check.getMaxVl())));

                alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + data.getPlayer().getName()));
                alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ColorUtil.translate(
                        "&cDescription: &f" + "Checks for FastBow (Bow Timer)." +
                                "\n&cInfo: &7" + info +
                                "\n&cPing: &7" + PacketEvents.get().getPlayerUtils().getPing(data.getPlayer()) +
                                "\n&cTPS: &7" + String.format("%.2f", Math.min(20, PacketEvents.get().getServerUtils().getTPS())) +
                                "\n&cClick to teleport.")).create()));
            }

            TextComponent finalAlertMessage = alertMessage;
            alerts.forEach(player -> player.getPlayer().spigot().sendMessage(finalAlertMessage));
        }
    }

    public static void handleAlertLag(final Check check, final PlayerData data, final String info) {
        if(check.getCheckInfo() != null) {
            alerts.forEach(player -> player.getPlayer().sendMessage(ColorUtil.translate(Config.PREFIX + player.getPlayer().getName() + " would flag for " + check.getFullName()) + ", but server skipped " + AntiHaxerman.INSTANCE.getTickManager().getA() + "MS/" + (AntiHaxerman.INSTANCE.getTickManager().getA() / 50) + " Ticks."));
        }
    }

    public static void sendMessage(final String message) {
        alerts.forEach(player -> player.getPlayer().sendMessage(ColorUtil.translate(Config.PREFIX + message)));
    }

    public enum ToggleAlertType {
        ADD, REMOVE
    }
}
