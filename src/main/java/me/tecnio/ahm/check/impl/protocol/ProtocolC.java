package me.tecnio.ahm.check.impl.protocol;

import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

@CheckManifest(name = "Protocol", type = "C", description = "Detects not swinging.")
public final class ProtocolC extends Check implements PacketCheck {

    private int streak;

    public ProtocolC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = ((GPacketPlayClientUseEntity) packet);

            if (wrapper.getType() == PlayerEnums.UseType.ATTACK) {
                if (++this.streak > 2) {
                    this.fail();
                }
            }
        } else if (packet instanceof GPacketPlayClientArmAnimation) {
            this.streak = 0;
        }
    }
}
