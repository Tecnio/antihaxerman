package me.tecnio.ahm.check.impl.interact;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientUseEntity;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

@CheckManifest(name = "Interact", type = "A", description = "Checks for hitting whilst placing.")
public final class InteractA extends Check implements PacketCheck {

    public InteractA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = ((GPacketPlayClientUseEntity) packet);

            final boolean place = data.getActionTracker().isPlacing();
            final boolean attack = wrapper.getType() == PlayerEnums.UseType.ATTACK;

            if (place && attack) this.fail();
        }
    }
}
