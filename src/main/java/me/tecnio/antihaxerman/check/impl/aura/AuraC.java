package me.tecnio.antihaxerman.check.impl.aura;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.entity.Entity;

@CheckInfo(name = "Aura", type = "C")
public final class AuraC extends Check {
    public AuraC(PlayerData data) {
        super(data);
    }

    private int ticks;
    private Entity lastTarget;

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        final Entity target = wrapper.getEntity();

        if (target != lastTarget) {
            if ((data.getDeltaYaw() == 0.0 && ticks <= 5) || (data.getDeltaYaw() > 5 && ticks < 2)) {
                if (increaseBuffer() > 1) {
                    flag();
                }
            } else {
                resetBuffer();
            }
        }

        ticks = 0;
        lastTarget = target;
    }

    @Override
    public void onFlying() {
        ticks++;
    }
}
