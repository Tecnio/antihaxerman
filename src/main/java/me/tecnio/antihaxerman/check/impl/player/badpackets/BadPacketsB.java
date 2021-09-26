

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;

@CheckInfo(name = "BadPackets", type = "B", description = "Checks for invalid sprint packets.")
public final class BadPacketsB extends Check {
    public BadPacketsB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isEntityAction()) {
            final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(packet.getRawPacket());

            final boolean sprinting = wrapper.getAction() == WrappedPacketInEntityAction.PlayerAction.START_SPRINTING
                    || wrapper.getAction() == WrappedPacketInEntityAction.PlayerAction.STOP_SPRINTING;

            if (sprinting) {
                if (increaseBuffer() > 1) {
                    fail();
                }
            }
        } else if (packet.isFlying()) {
            resetBuffer();
        }
    }
}
