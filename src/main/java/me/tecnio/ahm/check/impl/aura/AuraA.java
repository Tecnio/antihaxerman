package me.tecnio.ahm.check.impl.aura;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientUseEntity;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

/**
 * Check to detect players attacking multiple entities in a single tick, indicating multi aura-like behavior.
 */
@CheckManifest(name = "Aura", type = "A", description = "Detects attacking multiple entities in a tick.")
public final class AuraA extends Check implements PacketCheck {

    // ID of the last attacked entity
    private int lastTarget;
    // The amount of attacks sent in a tick
    private int attacks;

    public AuraA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = ((GPacketPlayClientUseEntity) packet);

            if (wrapper.getType() == PlayerEnums.UseType.ATTACK) {
                final int target = wrapper.getEntityId();

                // If the target entity is different from the last attacked entity and multiple attacks have occurred.
                if (target != this.lastTarget && ++this.attacks > 1) {
                    this.fail("A: %s", this.attacks);
                }

                this.lastTarget = target;
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            // Reset attack count when the player is flying (tick changes).
            this.attacks = 0;
        }
    }
}
