

package me.tecnio.antihaxerman.check.impl.movement.liquidspeed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "LiquidSpeed", type = "B", description = "Checks for vertical speed under water.")
public final class LiquidSpeedB extends Check {
    public LiquidSpeedB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean inLiquid = data.getPositionProcessor().isFullySubmergedInLiquidStat();

            final double multiplier = data.getPositionProcessor().isInWater() ? 0.8 : 0.5;

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double lastDeltaY = data.getPositionProcessor().getLastDeltaY();

            final double acceleration = deltaY - lastDeltaY;

            final double predictedY = (lastDeltaY + 0.03999999910593033D) * multiplier - 0.02D;;
            final double difference = Math.abs(deltaY - predictedY);

            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                return;
            }

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.VEHICLE, ExemptType.FLYING,
                    ExemptType.PISTON, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.WEB,
                    ExemptType.SLIME, ExemptType.BOAT, ExemptType.CHUNK);
            final boolean invalid = difference > 0.075 && deltaY > 0.075 && acceleration >= 0.0 && inLiquid;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail(difference);
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
