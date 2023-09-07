package me.tecnio.ahm.check.impl.protocol;

import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

@CheckManifest(name = "Protocol", type = "B", description = "Detects for self interaction.")
public final class ProtocolB extends Check implements PacketCheck {

    public ProtocolB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = (GPacketPlayClientUseEntity) packet;

            if (data.getPlayer().getEntityId() == wrapper.getEntityId()) {
                this.fail();
            }
        }
    }
}
