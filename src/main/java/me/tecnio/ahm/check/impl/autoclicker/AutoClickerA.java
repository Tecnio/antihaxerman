package me.tecnio.ahm.check.impl.autoclicker;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientArmAnimation;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

@CheckManifest(name = "AutoClicker", type = "A", description = "Detects higher click speeds.")
public final class AutoClickerA extends Check implements PacketCheck {

    private int cps, movements;

    public AutoClickerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            if (++this.movements >= 20) {
                if (this.cps > 20 && !data.getActionTracker().isPlacing()) {
                    this.fail("C: %s", this.cps);
                }

                this.cps = 0;
                this.movements = 0;
            }
        }

        else if (packet instanceof GPacketPlayClientArmAnimation) {
            if (!data.getActionTracker().isPlacing()) {
                ++this.cps;
            }
        }
    }
}