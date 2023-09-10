package me.tecnio.ahm.check.impl.aura;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientArmAnimation;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientUseEntity;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientFlying;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

@CheckManifest(name = "Aura", type = "C", description = "Checks for no swing cheats")
public class AuraC extends Check implements PacketCheck {

    private int swings, hits;
    private long lastAttack;

    public AuraC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(GPacket packet) {
        if (packet instanceof PacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = (GPacketPlayClientUseEntity) packet;

            if (wrapper.getType() != PlayerEnums.UseType.ATTACK) return;

            final long currentTime = System.currentTimeMillis();
            final long delta = currentTime - this.lastAttack;

            this.lastAttack = currentTime;

            if (this.hits++ > 3) {
                if (this.swings == 0 && delta > 25L) {
                    fail("swings=" + swings + ", delta=" + delta);
                }

                this.hits = this.swings = 0;
            }
        } else if (packet instanceof PacketPlayClientArmAnimation) {
            this.swings++;
        }
    }
}
