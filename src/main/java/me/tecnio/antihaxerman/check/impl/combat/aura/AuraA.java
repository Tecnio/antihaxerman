package me.tecnio.antihaxerman.check.impl.combat.aura;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@CheckInfo(name = "Aura", type = "A", description = "Checks if player is sprinting after attack.")
public final class AuraA extends Check {
    public AuraA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && hitTicks() < 2) {
            final Entity target = data.getCombatProcessor().getTarget();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double baseSpeed = PlayerUtil.getBaseSpeed(data.getPlayer(), 0.22F);
            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);

            final boolean exempt = !(target instanceof Player);
            final boolean invalid = acceleration < 0.0027 && sprinting && deltaXZ > baseSpeed;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }
}
