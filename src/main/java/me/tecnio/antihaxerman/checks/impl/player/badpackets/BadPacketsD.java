package me.tecnio.antihaxerman.checks.impl.player.badpackets;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "BadPackets", type = "D")
public final class BadPacketsD extends Check {
    public BadPacketsD(PlayerData data) {
        super(data);
    }

    private long armSwingTime;

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if(e.getPacketId() == PacketType.Client.ARM_ANIMATION) {
            armSwingTime = time();
        } else if(e.getPacketId() == PacketType.Client.USE_ENTITY) {
            final WrappedPacketInUseEntity packet = new WrappedPacketInUseEntity(e.getNMSPacket());

            if(packet.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                long attackTime = time();
                long diff = armSwingTime - attackTime;
                if(diff < -100) { if (++preVL > 2){ flag(data, "noswing, delay: " + diff); }
                }else preVL = 0;
            }
        }
    }
}
