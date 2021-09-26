package me.tecnio.antihaxerman.command.impl;

import me.tecnio.antihaxerman.command.AntiHaxermanCommand;
import me.tecnio.antihaxerman.command.CommandInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.util.BotTypes;
import me.tecnio.antihaxerman.util.BotUtils;
import me.tecnio.antihaxerman.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "forcebot", syntax = "<player> <mode>", purpose = "Creates a bot next to the player, MODES: NORMAL, WATCHDOG, FOLLOW")
public class ForceBot extends AntiHaxermanCommand {
    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.translate("&cThis command must be executed in-game!"));
            return true;
        }
        if(args.length >= 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null) {
                sender.sendMessage(ColorUtil.translate("&cThis player does not exist!"));
                return true;
            }
            BotTypes botType;
            try {
                botType = BotTypes.valueOf(args[2]);
            } catch (IllegalArgumentException Exception) {
                sender.sendMessage(ColorUtil.translate("&cThis mode does not exist!"));
                return true;
            }
            PlayerData attackerData = PlayerDataManager.getInstance().getPlayerData((Player) sender);
            PlayerData victimData = PlayerDataManager.getInstance().getPlayerData(target);
            if(victimData.getCombatProcessor().getTarget() == null) {
                sender.sendMessage(ColorUtil.translate("&cThis player doesnt have any targets!"));
                return true;
            }
            if(victimData.getBotProcessor().hasBot) {
                sender.sendMessage(ColorUtil.translate("&cThis player is already being checked by a bot!"));
                return true;
            }
            BotUtils.spawnBotEntity(victimData, attackerData, botType);
            sender.sendMessage(ColorUtil.translate("&aSUCESS! &aSending this bot to the player!"));
            return true;
        }
        return false;
    }
}
