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

package me.tecnio.antihaxerman.check.impl.player.inventory;

import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Inventory", type = "D", description = "Checks if player is swinging or attacking while opening inventory.")
public final class InventoryD extends Check {

    private boolean attacking, swinging;

    public InventoryD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isClientCommand()) {
            final WrappedPacketInClientCommand wrapper = new WrappedPacketInClientCommand(packet.getRawPacket());

            if (wrapper.getClientCommand() != WrappedPacketInClientCommand.ClientCommand.OPEN_INVENTORY_ACHIEVEMENT) return;

            if (attacking || swinging) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                resetBuffer();
            }

        } else if (packet.isFlying()) {
            attacking = false;
            swinging = false;
        } else if (packet.isArmAnimation()) {
            swinging = true;
        } else if (packet.isUseEntity()) {
            attacking = true;
        }
    }
}
