package me.tecnio.antihaxerman.commands;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AntiHaxermanCommand implements CommandExecutor {

    private static final String seperator = "----------------------";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player){
            if (commandSender.hasPermission("antihaxerman.command") || commandSender.isOp()){
                PlayerData data = DataManager.INSTANCE.getUser(((Player) commandSender).getUniqueId());

                if (s.equalsIgnoreCase("antihaxerman")){
                    if (strings.length <= 0){
                        commandSender.sendMessage(ChatColor.DARK_GREEN + seperator);
                        commandSender.sendMessage(ChatColor.WHITE + "/antihaxerman alerts : Toggles alerts.");
                        commandSender.sendMessage(ChatColor.WHITE + "/antihaxerman info <player> : Shows info about player.");
                        commandSender.sendMessage(ChatColor.DARK_GREEN + seperator);
                        return true;
                    }else{
                        if (strings[0] != null && strings[0].equalsIgnoreCase("alerts")){
                            if(commandSender.hasPermission("antihaxerman.alerts") || commandSender.isOp()){
                                sendMessage(commandSender, data.isAlerts() ? ChatColor.RED + "Disabled alerts." : ChatColor.DARK_GREEN + "Enabled alerts.");
                                data.setAlerts(!data.isAlerts());
                                return true;
                            }
                        }else if (strings[0] != null && strings[0].equalsIgnoreCase("info")){
                            if (strings.length > 1){
                                Player player = Bukkit.getPlayerExact(strings[1]);
                                if (player != null){
                                    PlayerData playerData = DataManager.INSTANCE.getUser(player.getUniqueId());
                                    commandSender.sendMessage(ChatColor.DARK_GREEN + seperator);
                                    commandSender.sendMessage(ChatColor.WHITE + "Ping: " + playerData.getPing());
                                    commandSender.sendMessage(ChatColor.WHITE + "Total flags: " + playerData.getTotalFlags());
                                    commandSender.sendMessage(ChatColor.WHITE + "Logs:");
                                    for (Check check : playerData.getChecks()) { if (check.vl > 0) { commandSender.sendMessage(ChatColor.GREEN + check.checkName + " : " + check.checkType + " : " + check.vl + "VL"); } }
                                    commandSender.sendMessage(ChatColor.DARK_GREEN + seperator);
                                    return true;
                                }else sendMessage(commandSender, ChatColor.RED + "Cannot find player!");return true;
                            }else sendMessage(commandSender, ChatColor.RED + "Usage: /antihaxerman info <player>");return true;
                        }
                    }
                }
            }else sendMessage(commandSender, ChatColor.RED + "No permission!");
        }else sendMessage(commandSender, ChatColor.RED + "You have to be a player to do that.");

        return false;
    }

    private void sendMessage(CommandSender commandSender, String msg){
        commandSender.sendMessage(ChatUtils.color( "&aAntiHaxerman &8» " + ChatColor.RESET + msg));
    }
}
