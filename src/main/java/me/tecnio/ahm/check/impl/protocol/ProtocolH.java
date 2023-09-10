package me.tecnio.ahm.check.impl.protocol;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientKeepAlive;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientKeepAlive;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

@CheckManifest(name = "Protocol", type = "H", description = "Checks for duplicated packet ids")
public class ProtocolH extends Check implements PacketCheck {

    private long lastId = -1;

    public ProtocolH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientKeepAlive) {
            final GPacketPlayClientKeepAlive wrapper = (GPacketPlayClientKeepAlive) packet;

            final long id = wrapper.getId();

            if (id == this.lastId) {
                fail("id=" + id + ", last=" + lastId);
            }

            this.lastId = id;
        }
    }
}
