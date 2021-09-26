

package me.tecnio.antihaxerman.check.impl.movement.noslow;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "NoSlow", type = "B", description = "Checks if player is sneaking and sprinting.")
public final class NoSlowB extends Check {
    public NoSlowB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final int groundTicks = data.getPositionProcessor().getGroundTicks();

            final int sprintingTicks = data.getActionProcessor().getSprintingTicks();
            final int sneakingTicks = data.getActionProcessor().getSneakingTicks();
            final boolean exempt = isExempt(ExemptType.CHUNK) || groundTicks < 10;
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                if(this.isExempt(ExemptType.WEB, ExemptType.LIQUID)) {
                    return;
                }
            }
            final boolean invalid = sprintingTicks > 10 && sneakingTicks > 10;

            if (invalid && !exempt) {
                if (increaseBuffer() > 10) {
                    fail();
                }
            } else {
                resetBuffer();
            }
        }
    }
}
