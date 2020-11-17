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

package me.tecnio.antihaxerman.check.impl.nofall;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;
import me.tecnio.antihaxerman.utils.player.CollisionUtils;

@CheckInfo(name = "NoFall", type = "A")
public final class NoFallA extends Check {
    public NoFallA(PlayerData data) {
        super(data);
    }

    private int ticksSinceInVehicle;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (PacketUtils.isFlyingPacket(event.getPacketId())) {
            if (data.getPlayer().isInsideVehicle()) {
                ticksSinceInVehicle = 0;
            } else {
                ++ticksSinceInVehicle;
            }

            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(event.getNMSPacket());

            final boolean clientOnGround = wrapper.isOnGround();
            final boolean serverOnGround = CollisionUtils.isOnGround(data);

            final boolean exempt = (data.getLastFlying() - data.getLastLastFlying()) < 1 || data.isLagging() || !data.isTouchingAir() || data.liquidTicks() < 10 || data.climbableTicks() < 10 || ticksSinceInVehicle < 10 || data.isNearBoat();

            if (serverOnGround != clientOnGround && !exempt) {
                if (increaseBuffer() > 15) {
                    flag();
                }
            } else {
                resetBuffer();
            }
        }
    }
}
