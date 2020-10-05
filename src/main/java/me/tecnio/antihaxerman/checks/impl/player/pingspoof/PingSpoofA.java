package me.tecnio.antihaxerman.checks.impl.player.pingspoof;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "PingSpoof", type = "A")
public class PingSpoofA extends Check {
    public PingSpoofA(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (isFlyingPacket(e)) {
            if (data.teleportTicks() < 10 || data.getTicks() < 10) buffer = 0;

            if (data.teleportTicks() > 100 && data.getTicks() > 100) {
                final double diff = Math.abs(data.getTransactionPing() - data.getPing());

                if (diff > 50) {
                    if (++buffer > 200) {
                        flag(data, "transaction ping different than keep alive. diff: " + diff);
                    }
                } else buffer = Math.max(buffer - 10, 0);
            }
        }
    }
}
