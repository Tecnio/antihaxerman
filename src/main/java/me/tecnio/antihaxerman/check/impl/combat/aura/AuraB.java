

package me.tecnio.antihaxerman.check.impl.combat.aura;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Entity;

@CheckInfo(name = "Aura", type = "B", description = "Checks for multi-aura.")
public final class AuraB extends Check {
    public AuraB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                final Entity target = data.getCombatProcessor().getTarget();
                final Entity lastTarget = data.getCombatProcessor().getLastTarget();

                final boolean exempt = target == lastTarget;

                if (!exempt) {
                    if (increaseBuffer() > 1) {
                        fail();
                    }
                }
            }
        } else if (packet.isFlying()) {
            resetBuffer();
        }
    }
}
