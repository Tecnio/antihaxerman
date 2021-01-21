package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Aim", type = "C", description = "Checks for invalid sensitivity.")
public final class AimC extends Check {
    public AimC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final double sensitivity = data.getRotationProcessor().getSensitivity();

            final boolean exempt = data.getRotationProcessor().isCinematic();
            final boolean invalid = sensitivity < 0.0F;

            if (invalid && !exempt) {
                if (increaseBuffer() > 5) {
                    fail();
                }
            } else {
                decreaseBufferBy(2);
            }
        }
    }
}
