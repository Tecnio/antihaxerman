package me.tecnio.ahm.check.impl.interact;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientBlockPlace;
import cc.ghast.packet.wrapper.bukkit.Vector3D;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

import java.util.stream.Stream;

@CheckManifest(name = "Interact", type = "B", description = "Detects invalid block placements.")
public class InteractB extends Check implements PacketCheck {

    public InteractB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientBlockPlace) {
            final GPacketPlayClientBlockPlace wrapper = ((GPacketPlayClientBlockPlace) packet);

            final Vector3D cursor = wrapper.getVector();

            final float x = cursor.getX();
            final float y = cursor.getY();
            final float z = cursor.getZ();

            if (Stream.of(x, y, z).anyMatch(v -> v > 1.0F || v < 0.0F)) this.fail();
        }
    }
}
