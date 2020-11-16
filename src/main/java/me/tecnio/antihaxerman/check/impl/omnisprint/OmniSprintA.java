package me.tecnio.antihaxerman.check.impl.omnisprint;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@CheckInfo(name = "OmniSprint", type = "A")
public final class OmniSprintA extends Check {
    public OmniSprintA(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final Vector move = new Vector(data.getDeltaX(), 0, data.getDeltaZ());
        final double delta = move.distanceSquared(data.getDirection());

        final boolean exempt = data.getDeltaXZ() < 0.1 || data.liquidTicks() < 20 || data.isInWeb() || data.isTakingVelocity() || !data.isOnGround();
        final boolean invalid = delta >= getLimit() && data.isSprinting();

        if (invalid && !exempt) {
            if (increaseBuffer() > 4) {
                flag();
            }
        } else {
            resetBuffer();
        }
    }

    private double getLimit() {
        return data.getPlayer().getWalkSpeed() > 0.2f ? .23 * 1 + ((data.getPlayer().getWalkSpeed() / 0.2f) * 0.36) : 0.23 + (PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.062f);
    }
}
