package me.tecnio.ahm.check.impl.aura;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientArmAnimation;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientUseEntity;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

@CheckManifest(name = "Aura", type = "C", description = "Checks for no swing.", threshold = 1)
public class AuraC extends Check implements PacketCheck {

    private boolean swung;

    public AuraC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = ((GPacketPlayClientUseEntity) packet);

            if (wrapper.getType() == PlayerEnums.UseType.ATTACK && !this.swung) this.fail();
        }

        else if (packet instanceof PacketPlayClientArmAnimation) {
            this.swung = true;
        }

        else if (packet instanceof PacketPlayClientFlying) {
            this.swung = false;
        }
    }
}
