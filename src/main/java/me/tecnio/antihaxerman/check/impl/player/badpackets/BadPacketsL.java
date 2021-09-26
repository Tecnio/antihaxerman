

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "BadPackets", type = "L", description = "Checks for 0 rotation with a rotation packet.")
public final class BadPacketsL extends Check {
    public BadPacketsL(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.VEHICLE);
            final boolean invalid = deltaPitch == 0.0F && deltaYaw  == 0.0F;

        }
    }
}
