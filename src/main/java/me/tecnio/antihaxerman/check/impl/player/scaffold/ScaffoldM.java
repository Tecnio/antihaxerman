package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;

@CheckInfo(name = "Scaffold", type = "M", description = "Checks for wrong interaction with a block.")
public class ScaffoldM extends Check {

    public ScaffoldM(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            final float x = wrapper.getCursorPosition().get().x;
            final float y = wrapper.getCursorPosition().get().y;
            final float z = wrapper.getCursorPosition().get().z;

            for (final float value : new float[]{x, y, z}) {
                if (value > 1.0 || value < 0.0) fail();
            }
        }
    }
}
