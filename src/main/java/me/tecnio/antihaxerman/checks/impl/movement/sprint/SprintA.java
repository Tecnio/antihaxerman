package me.tecnio.antihaxerman.checks.impl.movement.sprint;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.checks.SetBackType;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@CheckInfo(name = "Sprint", type = "A")
public class SprintA extends Check {
    @Override
    public void onMove(PlayerData data) {
        Vector move = new Vector(data.getLocation().getX() - data.getLastLocation().getX(), 0, data.getLocation().getZ() - data.getLastLocation().getZ());
        double delta = move.distanceSquared(data.getDirection());
        if (delta >= (data.getPlayer().getWalkSpeed() > 0.2f ? .23 * 1 + ((data.getPlayer().getWalkSpeed() / 0.2f) * 0.36) : 0.23 + (PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.062f)) && data.isSprinting() && data.getDeltaXZ() > 0.1 && !PlayerUtils.inLiquid(data) && !PlayerUtils.isInWeb(data) && !data.isTakingVelocity() && data.isOnGround()) {
            if (++preVL > 4) {
                flag(data, "omnidirectional sprint, p: " + delta, SetBackType.BACK);
            }
        } else preVL = 0;
    }
}
