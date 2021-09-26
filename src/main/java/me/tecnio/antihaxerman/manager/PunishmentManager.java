package me.tecnio.antihaxerman.manager;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.api.APIManager;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.util.ColorUtil;
import me.tecnio.antihaxerman.util.LogUtil;
import me.tecnio.antihaxerman.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public final class PunishmentManager {
    public static void punish(final Check check, final PlayerData data) {
        APIManager.callPunishEvent(check);
        if(Config.TESTMODE) {
            return;
        }
        check.setPunishCommands((ArrayList<String>) Config.PUNISH_COMMANDS.get(check.getClass().getSimpleName()));
        if(check.custom == 0) {
            if(!Config.GLOBALCMD) {
                if (check.getPunishCommands() != null) {
                    for(String s : check.getPunishCommands()) {
                        if(!s.equals("")) {
                            s = ChatColor.translateAlternateColorCodes('&', s);
                            s = s.replaceAll("%player%", data.getPlayer().getName())
                                    .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                    .replaceAll("%check%", check.getCheckInfo().name())
                                    .replaceAll("%vl%", String.valueOf(check.getVl()))
                                    .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                    .replaceAll("%type%", check.getCheckInfo().type());
                            String finalS = s;
                            Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                        }
                    }
                }
                if(Config.BANTIMER) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for(String s : Config.TIMER_COMMANDS) {
                                if(!s.equals("")) {
                                    s = ChatColor.translateAlternateColorCodes('&', s);
                                    s = s.replaceAll("%player%", data.getPlayer().getName())
                                            .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                            .replaceAll("%check%", check.getCheckInfo().name())
                                            .replaceAll("%vl%", String.valueOf(check.getVl()))
                                            .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                            .replaceAll("%type%", check.getCheckInfo().type());
                                    String finalS = s;
                                    Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                                }
                            }
                        }
                    }.runTaskLater(AntiHaxerman.INSTANCE.getPlugin(), Config.BANTIMERTIME * 20L);
                }
            } else {
                for(String s : Config.GLOBAL_COMMANDS) {
                    if(!s.equals("")) {
                        s = ChatColor.translateAlternateColorCodes('&', s);
                        s = s.replaceAll("%player%", data.getPlayer().getName())
                                .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                .replaceAll("%check%", check.getCheckInfo().name())
                                .replaceAll("%vl%", String.valueOf(check.getVl()))
                                .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                .replaceAll("%type%", check.getCheckInfo().type());
                        String finalS = s;
                        Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                    }
                }
                if(Config.BANTIMER) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for(String s : Config.TIMER_COMMANDS) {
                                if(!s.equals("")) {
                                    s = ChatColor.translateAlternateColorCodes('&', s);
                                    s = s.replaceAll("%player%", data.getPlayer().getName())
                                            .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                            .replaceAll("%check%", check.getCheckInfo().name())
                                            .replaceAll("%vl%", String.valueOf(check.getVl()))
                                            .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                            .replaceAll("%type%", check.getCheckInfo().type());
                                    String finalS = s;
                                    Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                                }
                            }
                        }
                    }.runTaskLater(AntiHaxerman.INSTANCE.getPlugin(), Config.BANTIMERTIME * 20L);
                }
            }

            if (Config.LOGGING_ENABLED) {
                final String log = String.format("###\n%s has been punished for %s (Type %s)\n" +
                                "Info:\n" +
                                "kaPing: %.2d tPing: %.2d lag: %s\n" +
                                "TPS: %.2f\n" +
                                "deltaXZ: %.4f deltaY: %.4f" +
                                "\n###",
                        data.getPlayer().getName(), check.getCheckInfo().name(),
                        check.getCheckInfo().type(), data.getConnectionProcessor().getKeepAlivePing(),
                        data.getConnectionProcessor().getTransactionPing(), data.getExemptProcessor().isExempt(ExemptType.LAGGING),
                        ServerUtil.getTPS(), data.getPositionProcessor().getDeltaXZ(), data.getPositionProcessor().getDeltaY());

                LogUtil.logToFile(data.getLogFile(), log);
            }
        }
        else if(check.custom == 1) {
            if(!Config.GLOBALCMD) {
                if (check.getPunishCommands() != null) {
                    for(String s : check.getPunishCommands()) {
                        if(!s.equals("")) {
                            s = ChatColor.translateAlternateColorCodes('&', s);
                            s = s.replaceAll("%player%", data.getPlayer().getName())
                                    .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                    .replaceAll("%check%", check.getCheckInfo().name())
                                    .replaceAll("%vl%", String.valueOf(check.getVl()))
                                    .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                    .replaceAll("%type%", check.getCheckInfo().type());
                            String finalS = s;
                            Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                        }
                    }
                }
                if(Config.BANTIMER) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for(String s : Config.TIMER_COMMANDS) {
                                if(!s.equals("")) {
                                    s = ChatColor.translateAlternateColorCodes('&', s);
                                    s = s.replaceAll("%player%", data.getPlayer().getName())
                                            .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                            .replaceAll("%check%", "Speed")
                                            .replaceAll("%vl%", String.valueOf(check.getVl()))
                                            .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                            .replaceAll("%type%", "F");
                                    String finalS = s;
                                    Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                                }
                            }
                        }
                    }.runTaskLater(AntiHaxerman.INSTANCE.getPlugin(), Config.BANTIMERTIME * 20L);
                }
            } else {
                for(String s : Config.GLOBAL_COMMANDS) {
                    if(!s.equals("")) {
                        s = ChatColor.translateAlternateColorCodes('&', s);
                        s = s.replaceAll("%player%", data.getPlayer().getName())
                                .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                .replaceAll("%check%", "Speed")
                                .replaceAll("%vl%", String.valueOf(check.getVl()))
                                .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                .replaceAll("%type%", "F");
                        String finalS = s;
                        Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                    }
                }
                if(Config.BANTIMER) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for(String s : Config.TIMER_COMMANDS) {
                                if(!s.equals("")) {
                                    s = ChatColor.translateAlternateColorCodes('&', s);
                                    s = s.replaceAll("%player%", data.getPlayer().getName())
                                            .replaceAll("%prefix%", ColorUtil.translate(Config.PREFIX))
                                            .replaceAll("%check%", "Speed")
                                            .replaceAll("%vl%", String.valueOf(check.getVl()))
                                            .replaceAll("%maxvl%", String.valueOf(check.getMaxVl()))
                                            .replaceAll("%type%", "F");
                                    String finalS = s;
                                    Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), finalS));
                                }
                            }
                        }
                    }.runTaskLater(AntiHaxerman.INSTANCE.getPlugin(), Config.BANTIMERTIME * 20L);
                }
            }

            if (Config.LOGGING_ENABLED) {
                final String log = String.format("###\n%s has been punished for %s (Type %s)\n" +
                                "Info:\n" +
                                "kaPing: %.2d tPing: %.2d lag: %s\n" +
                                "TPS: %.2f\n" +
                                "deltaXZ: %.4f deltaY: %.4f" +
                                "\n###",
                        data.getPlayer().getName(), check.getCheckInfo().name(),
                        check.getCheckInfo().type(), data.getConnectionProcessor().getKeepAlivePing(),
                        data.getConnectionProcessor().getTransactionPing(), data.getExemptProcessor().isExempt(ExemptType.LAGGING),
                        ServerUtil.getTPS(), data.getPositionProcessor().getDeltaXZ(), data.getPositionProcessor().getDeltaY());

                LogUtil.logToFile(data.getLogFile(), log);
            }
        }
    }
}