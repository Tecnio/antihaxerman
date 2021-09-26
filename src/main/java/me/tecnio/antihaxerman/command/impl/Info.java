

package me.tecnio.antihaxerman.command.impl;

import me.tecnio.antihaxerman.command.AntiHaxermanCommand;
import me.tecnio.antihaxerman.command.CommandInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.util.ColorUtil;
import io.github.retrooper.packetevents.PacketEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "info", syntax = "<player>", purpose = "Returns information about the players client.")
public final class Info extends AntiHaxermanCommand {
    @Override
    protected boolean handle(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            final Player player = Bukkit.getPlayer(args[1]);

            if (player != null) {
                final PlayerData playerData = PlayerDataManager.getInstance().getPlayerData(player);

                if (playerData != null) {
                    sendLineBreak(sender);
                    sendPrefix(sender);
                    sendMessage(sender, ColorUtil.translate("&cInformation for &c" + playerData.getPlayer().getName() + "&a."));
                    sendRetardedNewLine(sender);
                    sendMessage(sender, ColorUtil.translate("&2&oGeneral information:"));
                    sendMessage(sender, ColorUtil.translate("&cLatency → &2" + PacketEvents.get().getPlayerUtils().getPing(playerData.getPlayer())) + "ms");
                    sendMessage(sender, ColorUtil.translate("&cClient Version → &2" + PacketEvents.get().getPlayerUtils().getClientVersion(playerData.getPlayer())));
                    sendMessage(sender, ColorUtil.translate("&cIs Bedrock Edition: → &2" + PacketEvents.get().getPlayerUtils().isGeyserPlayer(playerData.getPlayer())));
                    sendRetardedNewLine(sender);
                    sendMessage(sender, ColorUtil.translate("&2&oViolations information:"));
                    sendMessage(sender, ColorUtil.translate("&cTotal check violations → &2" + playerData.getTotalViolations()));
                    sendMessage(sender, ColorUtil.translate("&cCombat check violations → &2" + playerData.getCombatViolations()));
                    sendMessage(sender, ColorUtil.translate("&cMovement check violations → &2" + playerData.getMovementViolations()));
                    sendMessage(sender, ColorUtil.translate("&cPlayer check violations → &2" + playerData.getPlayerViolations()));
                    sendLineBreak(sender);
                    return true;
                }
            }
        }
        return false;
    }
}
