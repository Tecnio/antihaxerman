

package me.tecnio.antihaxerman.check.impl.movement.noslow;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "NoSlow", type = "C", description = "Checks if player is not slowing down while eating food.")
public final class NoSlowC extends Check {

    public NoSlowC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThan(ClientVersion.v_1_8)) {
                return;
            }
            final boolean sprinting = data.getActionProcessor().isSprinting();
            final boolean eating = data.getActionProcessor().isEating();
            final boolean holdingEdible = data.getPlayer().getItemInHand().getType().isEdible();
            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.BOAT, ExemptType.VEHICLE, ExemptType.CHUNK) || data.getPositionProcessor().isInAir();
            final boolean invalid = eating && sprinting && holdingEdible;

            if (invalid && !exempt) {
                if (getBuffer() > 5) data.getPlayer().setItemInHand(data.getPlayer().getItemInHand());

                if (increaseBuffer() > 8) {
                    fail();
                    data.getPlayer().setItemInHand(data.getPlayer().getItemInHand());
                }
            } else {
                decreaseBufferBy(2.5);
            }
        }
    }
}
