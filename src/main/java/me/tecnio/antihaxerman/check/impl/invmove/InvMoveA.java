/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

package me.tecnio.antihaxerman.check.impl.invmove;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;
import org.bukkit.GameMode;

@CheckInfo(name = "InvMove", type = "A")
public final class InvMoveA extends Check {
    public InvMoveA(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.WINDOW_CLICK) {
            final boolean exempt = data.isInWeb() || data.flyingTicks() < 20 || data.pistonTicks() < 10 || data.liquidTicks() < 20 || data.climbableTicks() < 20 || data.isTakingVelocity() || data.getPlayer().getGameMode().equals(GameMode.CREATIVE);
            final boolean invalid = data.getDeltaXZ() > PlayerUtils.getBaseSpeed(data.getPlayer(), 0.15F) && data.isOnGround();

            if (invalid && !exempt) {
                if (increaseBuffer() > 3) {
                    flag();
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
