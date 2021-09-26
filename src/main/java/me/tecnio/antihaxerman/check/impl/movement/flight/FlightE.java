package me.tecnio.antihaxerman.check.impl.movement.flight;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Flight", type = "E", description = "Enforces basic gravity rule.")
public class FlightE extends Check {
    private double minDelta;

    public FlightE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            if (this.isExempt(ExemptType.CHUNK, ExemptType.FLYING, ExemptType.GETTINGCOMBOED,ExemptType.PEARL, ExemptType.ICE, ExemptType.PLACING, ExemptType.SLIME, ExemptType.VOID, ExemptType.RESPAWN, ExemptType.VEHICLE, ExemptType.TELEPORT, ExemptType.GHOST_BLOCK, ExemptType.CREATIVE, ExemptType.COMBAT, ExemptType.UPWARDS_VEL)) {
                return;
            }
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                if(this.isExempt(ExemptType.NEARSLIME, ExemptType.PLACING)) {
                    return;
                }
            }
            final double dY = this.data.getPositionProcessor().getDeltaY();
            this.minDelta = Math.min(this.minDelta, dY);
            if (this.data.getPositionProcessor().getAirTicks() > 20) {
                if (dY > this.minDelta) {
                    fail("DeltaY: " + dY + " MinDelta: " + this.minDelta);
                }
            }
            else {
                this.minDelta = 0.0;
            }
        }
    }
}
