package me.tecnio.ahm.check.impl.scaffold;

import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.nms.EnumDirection;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

@CheckManifest(name = "Scaffold", type = "A", description = "Checks for downwards scaffold.")
public class ScaffoldA extends Check implements PacketCheck {

    public ScaffoldA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientBlockPlace) {
            final GPacketPlayClientBlockPlace blockPlace = (GPacketPlayClientBlockPlace) packet;

            blockPlace.getDirection().ifPresent(direction -> {
                final double offsetY = data.getPositionTracker().getY() - blockPlace.getPosition().getY();

                if (direction == EnumDirection.DOWN && offsetY >= 1) {
                    this.fail("o: %s", offsetY);
                }
            });
        }
    }
}
