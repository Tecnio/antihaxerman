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

package me.tecnio.antihaxerman.packet.processor;

import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

public final class ReceivingPacketProcessor  {
    public void handle(final PlayerData data, final Packet packet) {
        if (packet.isEntityAction()) {
            final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(packet.getRawPacket());

            data.getActionProcessor().handleEntityAction(wrapper);
        }
        if (packet.isBlockDig()) {
            final WrappedPacketInBlockDig wrapper = new WrappedPacketInBlockDig(packet.getRawPacket());

            data.getActionProcessor().handleBlockDig(wrapper);
        }
        if (packet.isClientCommand()) {
             final WrappedPacketInClientCommand wrapper = new WrappedPacketInClientCommand(packet.getRawPacket());

             data.getActionProcessor().handleClientCommand(wrapper);
        }
        if (packet.isBlockPlace()) {
            data.getActionProcessor().handleBlockPlace();
        }
        if (packet.isCloseWindow()) {
            data.getActionProcessor().handleCloseWindow();
        }
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            data.getCombatProcessor().handleUseEntity(wrapper);
        }
        if (packet.isFlying()) {
            data.setLastFlying(data.getFlying());
            data.setFlying(System.currentTimeMillis());

            data.getActionProcessor().handleFlying();
            data.getVelocityProcessor().handleFlying();
            data.getCombatProcessor().handleFlying();
            data.getClickProcessor().handleFlying();
            data.getConnectionProcessor().handleFlying();
        }
        if (packet.isPosition()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            data.getPositionProcessor().handle(wrapper.getX(), wrapper.getY(), wrapper.getZ(), wrapper.isOnGround());
        }
        if (packet.isRotation()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            data.getRotationProcessor().handle(wrapper.getYaw(), wrapper.getPitch());
        }
        if (packet.isArmAnimation()) {
            data.getClickProcessor().handleArmAnimation();

            data.getCombatProcessor().handleArmAnimation();
        }
        if (packet.isIncomingTransaction()) {
            final WrappedPacketInTransaction wrapper = new WrappedPacketInTransaction(packet.getRawPacket());

            data.getVelocityProcessor().handleTransaction(wrapper);
            data.getConnectionProcessor().handleIncomingTransaction(wrapper);
        }
        if (packet.isIncomingKeepAlive()) {
            final WrappedPacketInKeepAlive wrapper = new WrappedPacketInKeepAlive(packet.getRawPacket());

            data.getConnectionProcessor().handleIncomingKeepAlive(wrapper);
        }
        data.getChecks().forEach(check -> check.handle(packet));
    }
}