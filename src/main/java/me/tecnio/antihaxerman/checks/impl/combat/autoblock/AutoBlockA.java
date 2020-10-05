package me.tecnio.antihaxerman.checks.impl.combat.autoblock;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "AutoBlock", type = "A")
public final class AutoBlockA extends Check {
    public AutoBlockA(PlayerData data) {
        super(data);
    }

    private boolean attacked;
    private int ticks;

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (e.getPacketId() == PacketType.Client.USE_ENTITY){
            attacked = true;
        }else if (e.getPacketId() == PacketType.Client.BLOCK_PLACE){
            if(attacked) {
                if(ticks < 2) {
                    if (++buffer > 1)flag(data, "low tick delay, t: " + ticks);
                }else buffer = 0;
                attacked = false;
            }
            ticks = 0;
        }else if (isFlyingPacket(e)){
            ticks++;
        }
    }
}
