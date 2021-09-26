package me.tecnio.antihaxerman.check.impl.player.pingspoof;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "PingSpoof", type = "A", description = "Checks for keepalive packet and transaction packet difference.", experimental = true)
public final class PingSpoofA extends Check {
    public PingSpoofA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            final long transactionPing = data.getConnectionProcessor().getTransactionPing();
            final long keepAlivePing = data.getConnectionProcessor().getKeepAlivePing();

            final boolean exempt = isExempt(ExemptType.CHUNK, ExemptType.RESPAWN, ExemptType.AFK, ExemptType.LAGGING, ExemptType.TELEPORT_DELAY, ExemptType.JOINED, ExemptType.TPS, ExemptType.CHUNK);

            if (!exempt && transactionPing > keepAlivePing && Math.abs(transactionPing - keepAlivePing) > 100) fail();
        }
    }
}
