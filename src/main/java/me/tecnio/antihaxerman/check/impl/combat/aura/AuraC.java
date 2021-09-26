

package me.tecnio.antihaxerman.check.impl.combat.aura;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;

@CheckInfo(name = "Aura", type = "C", description = "checks for switch aura.")
public final class AuraC extends Check {

    private int ticks;
    private Entity lastTarget;


    public AuraC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isUseEntity()) {
            WrappedPacketInUseEntity packetwrapped = new WrappedPacketInUseEntity(packet.getRawPacket());
            Entity target = packetwrapped.getEntity();
            Entity lastTarget = this.lastTarget != null ? this.lastTarget : target;
            this.lastTarget = target;
            List<Entity> nearby = data.getCombatProcessor().getTarget().getNearbyEntities(3, 3, 3);
            if(nearby.size() >= 3) {
                return;
            }
            if (target != lastTarget) {
                if (ticks < 2) {
                    if(target.getType() == EntityType.PRIMED_TNT) {
                        return;
                    }
                    if (increaseBuffer() > 2) {
                        fail("switch aura, t: " + ticks);
                    }
                } else decreaseBuffer();
            }
            ticks = 0;
        } else if (packet.isFlying()) {
            ticks++;
        }
    }
}
