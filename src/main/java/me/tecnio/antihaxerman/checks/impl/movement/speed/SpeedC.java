package me.tecnio.antihaxerman.checks.impl.movement.speed;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "C")
public final class SpeedC extends Check {
    @Override
    public void onMove(PlayerData data) {
        double diff = data.getDeltaXZ() - data.getLastDeltaXZ();
        
        if (diff > getBaseSpeed(data.getPlayer())
                && !data.isTakingVelocity()
                && data.teleportTicks() > 20
                && !data.getPlayer().isInsideVehicle()
                && !data.getPlayer().isFlying()) {
            flag(data, "invalid acceleration, a: " + diff);
        }
    }

    private float getBaseSpeed(Player player) {
        return 0.34f + (PlayerUtils.getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }
}
