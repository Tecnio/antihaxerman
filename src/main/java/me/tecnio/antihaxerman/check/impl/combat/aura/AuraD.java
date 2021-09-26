

package me.tecnio.antihaxerman.check.impl.combat.aura;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Aura", type = "D", description = "checks if player's accuracy is bigger than 99%")
public final class AuraD extends Check {
    public AuraD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isUseEntity()) {
            final boolean invalid = data.getCombatProcessor().getHitMissRatio() > 99 &&
                    data.getRotationProcessor().getDeltaYaw() > 1.5F &&
                    data.getRotationProcessor().getDeltaPitch() > 0 &&
                    data.getPositionProcessor().getDeltaXZ() > 0.1;

            if (invalid) {
                if (increaseBuffer() > 25) {
                    fail("accuracy=" + data.getCombatProcessor().getHitMissRatio());
                }
            } else {
                decreaseBufferBy(2);
            }
        }
    }
}
