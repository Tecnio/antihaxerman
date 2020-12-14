package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Aim", type = "C", description = "Checks for invalid sensitivity.")
public final class AimC extends Check {
    public AimC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        final double sensitivity = data.getRotationProcessor().getSensitivity();

        if (sensitivity < 0.0F) {
            if (increaseBuffer() > 5) {
                fail();
            }
        } else {
            decreaseBuffer();
        }
    }
}
