package me.tecnio.antihaxerman.checks.impl.movement.speed;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "B")
public final class SpeedB extends Check {
    public SpeedB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        double limit = getBaseSpeed(data.getPlayer());

        if (elapsed(data.getTicks(), data.getIceTicks()) < 40 || elapsed(data.getTicks(), data.getSlimeTicks()) < 40) limit += 0.34;
        if (elapsed(data.getTicks(), data.getUnderBlockTicks()) < 40) limit += 0.91;
        if (data.isTakingVelocity()) limit += Math.hypot(Math.abs(data.getLastVel().getX()), Math.abs(data.getLastVel().getZ()));;

        if (data.getDeltaXZ() > limit
                && !data.getPlayer().isInsideVehicle()
                && !data.getPlayer().isFlying()
                && data.teleportTicks() > 10) {
            if (++preVL > 3) {
                flag(data, "breached limit, s: " + data.getDeltaXZ());
            }
        } else preVL *= 0.75;
    }

    private float getBaseSpeed(Player player) {
        return 0.34f + (PlayerUtils.getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }
}
