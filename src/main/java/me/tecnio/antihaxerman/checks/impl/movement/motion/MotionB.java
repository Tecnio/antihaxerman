package me.tecnio.antihaxerman.checks.impl.movement.motion;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "B")
public class MotionB extends Check {
    @Override
    public void onMove(PlayerData data) {
        if (!data.isInLiquid() && !data.getPlayer().isFlying() && !data.isInWeb() && data.teleportTicks() > 10) {
            double max = 0.7 + PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1;
            if (data.getDeltaY() > max && data.getPlayer().getVelocity().getY() < -0.075
                    && !data.isTakingVelocity()) {
                flag(data, "accelerating faster than possible on Y axis. d: " + data.getDeltaY());
            }
        }
    }
}
