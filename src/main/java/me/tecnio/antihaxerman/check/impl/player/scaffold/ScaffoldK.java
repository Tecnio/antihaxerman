package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import org.bukkit.GameMode;
import org.bukkit.util.Vector;

@CheckInfo(name = "Scaffold", type = "K", description = "Checks for block interaction distance.")
public class ScaffoldK extends Check {

    WrappedPacketInBlockPlace wrapper;

    public ScaffoldK(PlayerData data) {
        super(data);
    }


    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
        } else if (packet.isFlying()) {
            if (wrapper != null) {
                final Vector eyeLocation = data.getPlayer().getEyeLocation().toVector();
                final Vector blockLocation = new Vector(wrapper.getBlockPosition().x, wrapper.getBlockPosition().y, wrapper.getBlockPosition().z);

                final double deltaXZ = Math.abs(data.getPositionProcessor().getDeltaXZ());
                final double deltaY = Math.abs(data.getPositionProcessor().getDeltaY());

                final double maxDistance = data.getPlayer().getGameMode() == GameMode.CREATIVE ? 7.25 : 5.25;
                final double distance = eyeLocation.distance(blockLocation) - 0.7071 - deltaXZ - deltaY;

                final boolean exempt = blockLocation.getX() == -1.0 && blockLocation.getY() == -1.0 && blockLocation.getZ() == -1.0;
                final boolean invalid = distance > maxDistance;

                if (invalid && !exempt) {
                    if (increaseBuffer() > 1) {
                        fail(distance);
                    }
                } else {
                    decreaseBufferBy(0.05);
                }
            }

            wrapper = null;
        }
    }
}
