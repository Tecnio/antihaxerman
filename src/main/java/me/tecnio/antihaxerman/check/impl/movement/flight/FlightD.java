

package me.tecnio.antihaxerman.check.impl.movement.flight;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Flight", type = "D", description = "Checks for acceleration mid-air.")
public final class FlightD extends Check {
    public FlightD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                return;
            }
            final int serverAirTicks = data.getPositionProcessor().getAirTicks();
            final int clientAirTicks = data.getPositionProcessor().getClientAirTicks();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double acceleration = deltaY - lastDeltaY;

            final boolean exempt = isExempt(ExemptType.GHOST_BLOCK,ExemptType.LAGGINGHARD, ExemptType.BUKKIT_PLACING, ExemptType.VELOCITY, ExemptType.PISTON, ExemptType.VEHICLE,
                    ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.BOAT, ExemptType.FLYING,
                    ExemptType.WEB, ExemptType.SLIME, ExemptType.CLIMBABLE);
            final boolean invalid = acceleration > 0.0 && (serverAirTicks > 8 || clientAirTicks > 8);

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.1);
            }
        }
    }
}
