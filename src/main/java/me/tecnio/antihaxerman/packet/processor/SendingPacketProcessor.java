

package me.tecnio.antihaxerman.packet.processor;

import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;

public final class SendingPacketProcessor  {

    public void handle(final PlayerData data, final Packet packet) {
        if (packet.isVelocity()) {
            final WrappedPacketOutEntityVelocity wrapper = new WrappedPacketOutEntityVelocity(packet.getRawPacket());

            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                data.getVelocityProcessor().handle(wrapper.getVelocityX(), wrapper.getVelocityY(), wrapper.getVelocityZ());
            }
        }
        if (packet.isOutgoingTransaction()) {
            final WrappedPacketOutTransaction wrapper = new WrappedPacketOutTransaction(packet.getRawPacket());

            data.getConnectionProcessor().handleOutgoingTransaction(wrapper);
        }
        if (packet.isOutgoingKeepAlive()) {
            final WrappedPacketOutKeepAlive wrapper = new WrappedPacketOutKeepAlive(packet.getRawPacket());

            data.getConnectionProcessor().handleOutgoingKeepAlive(wrapper);
        }
        if (packet.isTeleport()) {
            final WrappedPacketOutPosition wrapper = new WrappedPacketOutPosition(packet.getRawPacket());

            data.getPositionProcessor().handleTeleport(wrapper);
        }
        data.getChecks().forEach(check -> check.handle(packet));
    }
}
