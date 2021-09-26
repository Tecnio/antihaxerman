

package me.tecnio.antihaxerman.packet.processor;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

public final class ReceivingPacketProcessor  {


    public void handle(final PlayerData data, final Packet packet) {
        if (PacketType.Play.Client.Util.isInstanceOfFlying(packet.getPacketId())) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());
            if (Math.abs(wrapper.getX()) > 1.0E+7
                    || Math.abs(wrapper.getY()) > 1.0E+7
                    || Math.abs(wrapper.getZ()) > 1.0E+7
                    || Math.abs(wrapper.getPitch()) > 1.0E+7
                    || Math.abs(wrapper.getYaw()) > 1.0E+7) {
                return;
            }
        }
        if (packet.isBlockDig()) {
            final WrappedPacketInBlockDig wrapper = new WrappedPacketInBlockDig(packet.getRawPacket());

            data.getActionProcessor().handleBlockDig(wrapper);
        }
        if(packet.isBukkitBlockPlace()) {
            data.getActionProcessor().handleBukkitPlace();
        }
        if (packet.isBlockPlace()) {
            data.getActionProcessor().handleBlockPlace();
        }
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            data.getCombatProcessor().handleUseEntity(wrapper);
        }
        if (packet.isEntityAction()) {
            final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(packet.getRawPacket());
            data.getActionProcessor().handleEntityAction(wrapper);
        }
        if (packet.isClientCommand()) {
             final WrappedPacketInClientCommand wrapper = new WrappedPacketInClientCommand(packet.getRawPacket());
             data.getActionProcessor().handleClientCommand(wrapper);
        }
        if (packet.isCloseWindow()) {
            data.getActionProcessor().handleCloseWindow();
        }
        if (packet.isFlying()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            ++data.existedTicks;
            data.getPositionProcessor().handle(wrapper);
            data.setLastFlying(data.getFlying());
            data.setFlying(System.currentTimeMillis());

            data.getActionProcessor().handleFlying();
            data.getVelocityProcessor().handleFlying();
            data.getCombatProcessor().handleFlying();
            data.getClickProcessor().handleFlying();
            data.getGhostBlockProcessor().handleFlying();
        }
        if (packet.isRotation()) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getRawPacket());

            data.getRotationProcessor().handle(wrapper.getYaw(), wrapper.getPitch());
        }
        if (packet.isArmAnimation()) {
            data.getClickProcessor().handleArmAnimation();
            data.getCombatProcessor().handleArmAnimation();
            data.getActionProcessor().handleArmAnimation();
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
        try {
            for(Check c : data.getChecks()) {
                if(c.isEnabled()) {
                    c.handle(packet);
                }
            }
        } catch(Exception ignore) {}
    }
}
