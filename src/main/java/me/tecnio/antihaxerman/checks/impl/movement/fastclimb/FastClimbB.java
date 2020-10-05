package me.tecnio.antihaxerman.checks.impl.movement.fastclimb;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "FastClimb", type = "B")
public final class FastClimbB extends Check {
    public FastClimbB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        if (data.isOnClimbableBlock() && !data.isTakingVelocity() && !data.isOnGround() && !data.isInWeb() && !data.getPlayer().isInsideVehicle() && !data.getPlayer().isFlying()){
            double hLimit = 0.24;
            if(PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.SPEED) > 0) { hLimit *= 1 + (PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.42); }
            if(data.getPlayer().getWalkSpeed() > 0.2f) { hLimit *= 1 + ((data.getPlayer().getWalkSpeed() / 0.2f) * 0.39); }
            if(data.getDeltaXZ() > hLimit) {
                if(buffer++ >= 2) {
                    flag(data, "moving faster than possible horizontally on a climbable");
                    buffer = 0;
                }
            } else buffer = Math.max(0, buffer--);
        }
    }
}
