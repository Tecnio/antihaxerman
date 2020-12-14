/*
 *  Copyright (C) 2020 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.command.impl;

import me.tecnio.antihaxerman.command.CommandInfo;
import me.tecnio.antihaxerman.command.AntiHaxermanCommand;
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
                    sendMessage(sender, ColorUtil.translate("&aInformation for &c" + playerData.getPlayer().getName() + "&a."));
                    sendRetardedNewLine(sender);
                    sendMessage(sender, ColorUtil.translate("&2&oGeneral information:"));
                    sendMessage(sender, ColorUtil.translate("&aLatency → &2" + PacketEvents.get().getPlayerUtils().getPing(playerData.getPlayer())) + "ms");
                    sendMessage(sender, ColorUtil.translate("&aChecks amount → &2" + playerData.getChecks().size()));
                    sendMessage(sender, ColorUtil.translate("&aSensitivity → &2" + playerData.getRotationProcessor().getSensitivity() + "%"));
                    final String clientBrand = playerData.getClientBrand() == null ? "&cCould not resolve client brand for this player." : playerData.getClientBrand();
                    sendMessage(sender, ColorUtil.translate("&aClient brand: → &2" + clientBrand));
                    sendRetardedNewLine(sender);
                    sendMessage(sender, ColorUtil.translate("&2&oViolations information:"));
                    sendMessage(sender, ColorUtil.translate("&aTotal check violations → &2" + playerData.getTotalViolations()));
                    sendMessage(sender, ColorUtil.translate("&aCombat check violations → &2" + playerData.getCombatViolations()));
                    sendMessage(sender, ColorUtil.translate("&aMovement check violations → &2" + playerData.getMovementViolations()));
                    sendMessage(sender, ColorUtil.translate("&aPlayer check violations → &2" + playerData.getPlayerViolations()));
                    sendLineBreak(sender);
                    return true;
                }
            }
        }
        return false;
    }
}
