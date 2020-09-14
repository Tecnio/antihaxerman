package me.tecnio.antihaxerman.checks.impl.player.nofall;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.checks.SetBackType;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "NoFall", type = "A")
public final class NoFallA extends Check {
    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())){
            if (new WrappedPacketInFlying(e.getNMSPacket()).isOnGround() && !PlayerUtils.onGround(data)){
                if (++preVL > 8){
                    flag(data, "spoofed ground.", SetBackType.PULLDOWN);
                }
            }else preVL = 0;
        }
    }
}
