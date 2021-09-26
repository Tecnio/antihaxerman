

package me.tecnio.antihaxerman.check.impl.player.badpackets;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "BadPackets", type = "A", description = "Checks if the player pitch is an impossible value.")
public final class BadPacketsA extends Check {
    public BadPacketsA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final float pitch = data.getRotationProcessor().getPitch();

            if (Math.abs(pitch)> 90.0f) {
                fail();
            }
        }
    }
}
