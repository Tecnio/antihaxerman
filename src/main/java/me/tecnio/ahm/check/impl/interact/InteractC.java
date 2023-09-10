package me.tecnio.ahm.check.impl.interact;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientBlockDig;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockDig;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

@CheckManifest(name = "Interact", type = "C", description = "Checks for baritone rotations")
public class InteractC extends Check implements PacketCheck {
    public InteractC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(GPacket packet) {
        if (packet instanceof PacketPlayClientBlockDig) {
            final GPacketPlayClientBlockDig wrapper = (GPacketPlayClientBlockDig) packet;

            if (wrapper.getType() != PlayerEnums.DigType.STOP_DESTROY_BLOCK
                    || Math.abs(data.getRotationTracker().getPitch()) == 90F) {
                return;
            }

            final float deltaPitch = data.getRotationTracker().getDeltaPitch();
            final float lastDeltaPitch = data.getRotationTracker().getLastDeltaPitch();

            final float delta = Math.abs(lastDeltaPitch - deltaPitch);

            if (delta < .005 && delta > 0) {
                fail("delta=" + delta);
            }
        }
    }
}
