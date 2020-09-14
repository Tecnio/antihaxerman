package me.tecnio.antihaxerman.checks.impl.player.badpackets;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "BadPackets", type = "C")
public final class BadPacketsC extends Check {

    private long lastFlying;

    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if(PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())){
            lastFlying = time();
        }else if (e.getPacketId() == PacketType.Client.USE_ENTITY){
            final long timeDiff = elapsed(time(), lastFlying);

            if (timeDiff < 5) {
                if (++preVL > 10) {
                    flag(data, "low flying delay, d: " + timeDiff);
                }
            } else preVL = 0;
        }
    }
}
