package me.tecnio.antihaxerman.check.impl.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "B")
public final class MotionB extends Check {
    public MotionB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final double max = 0.7 + PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1;
        final double deltaY = data.getDeltaY();

        final boolean exempt = data.getPlayer().getVelocity().getY() >= -0.075 || data.pistonTicks() < 10 || data.liquidTicks() < 20 || data.flyingTicks() < 20 || data.isInWeb() || data.teleportTicks() < 20;

        if (deltaY > max && !data.isTakingVelocity() && !exempt) {
            flag();
        } else if (deltaY > (max + data.getLastVelocity().getY()) && !exempt) {
            flag();
        }
    }
}
