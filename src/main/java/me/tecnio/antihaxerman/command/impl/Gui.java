package me.tecnio.antihaxerman.command.impl;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.command.AntiHaxermanCommand;
import me.tecnio.antihaxerman.command.CommandInfo;
import me.tecnio.antihaxerman.util.ColorUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "gui", syntax = "", purpose = "Opens Interactive GUI")
public class Gui extends AntiHaxermanCommand {
    @Override
    protected boolean handle(CommandSender sender, Command command, String label, String[] args) {
        if(args.length >= 2) {
            return false;
        }
        if(!(sender instanceof Player)) {
            sender.sendMessage(ColorUtil.translate("&cOnly players are allowed to execute this command!"));
            return true;
        }
        Player p = (Player) sender;
        AntiHaxerman.INSTANCE.getGuiManager().openMainMenu(p);
        p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);
        return true;
    }
}
