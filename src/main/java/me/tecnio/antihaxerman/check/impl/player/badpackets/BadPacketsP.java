package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "BadPackets", type = "P", description = "Checks for attacking and digging.")
public class BadPacketsP extends Check {

    public BadPacketsP(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThan(ClientVersion.v_1_8)) {
                return;
            }
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final boolean sword = this.data.getPlayer().getItemInHand().getType().toString().contains("SWORD");
                final boolean invalid = this.data.getActionProcessor().isSendingDig();
                if (invalid && sword) {
                    if (increaseBuffer() > 5.0) {
                    }
                }
                else {
                    setBuffer(Math.max(getBuffer() - 1.0, 0.0));
                }
            }
        }
    }
}
