package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import org.bukkit.util.Vector;

@CheckInfo(name = "Scaffold", type = "L", description = "Checks if player is looking at the block interacted.")
public class ScaffoldL extends Check {

    public ScaffoldL(PlayerData data) {
        super(data);
    }

    private WrappedPacketInBlockPlace wrapper = null;

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
        } else if (packet.isFlying()) {
            if (wrapper != null) {
                final Vector eyeLocation = data.getPlayer().getEyeLocation().toVector();
                final Vector blockLocation = new Vector(wrapper.getBlockPosition().x, wrapper.getBlockPosition().y, wrapper.getBlockPosition().z);

                final Vector directionToDestination = blockLocation.clone().subtract(eyeLocation);
                final Vector playerDirection = data.getPlayer().getEyeLocation().getDirection();

                final float angle = directionToDestination.angle(playerDirection);
                final float distance = (float) eyeLocation.distance(blockLocation);

                final boolean exempt = blockLocation.getX() == -1.0 && blockLocation.getY() == -1.0 && blockLocation.getZ() == -1.0;
                final boolean invalid = angle > 1.0F && distance > 1.5;

                if (invalid && !exempt) {
                    if (increaseBuffer() > 3) {
                        fail();
                    }
                } else {
                    decreaseBuffer();
                }
            }

            wrapper = null;
        }
    }
}
