package me.tecnio.antihaxerman.checks.impl.combat.aura;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "Aura", type = "A")
public final class AuraA extends Check {
    public AuraA(PlayerData data) {
        super(data);
    }

    private int hitTicks;

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (e.getPacketId() == PacketType.Client.POSITION_LOOK){
            if (++hitTicks < 2 && data.isSprinting()){
                if (Math.abs(data.getDeltaXZ() - data.getLastDeltaXZ()) < 0.002){
                    if (++preVL > 2){
                        flag(data, "not slowing down on head movement. accel: " + Math.abs(data.getDeltaXZ() - data.getLastDeltaXZ()));
                    }
                }else preVL = Math.max(0, preVL - 1);
            }
        }
    }
}
