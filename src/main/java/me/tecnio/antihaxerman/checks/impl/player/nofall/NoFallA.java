package me.tecnio.antihaxerman.checks.impl.player.nofall;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "NoFall", type = "A")
public final class NoFallA extends Check {
    public NoFallA(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())){
            if (new WrappedPacketInFlying(e.getNMSPacket()).isOnGround() && !PlayerUtils.onGround(data)){
                if (++preVL > 8){
                    flag(data, "spoofed ground.");
                }
            }else preVL = 0;
        }
    }
}
