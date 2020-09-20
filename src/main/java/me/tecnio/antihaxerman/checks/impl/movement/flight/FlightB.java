package me.tecnio.antihaxerman.checks.impl.movement.flight;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Flight", type = "B")
public final class FlightB extends Check {
    public FlightB(PlayerData data) {
        super(data);
    }

    @Override
    public void onMove() {
        final double limit = 6 + (PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.JUMP));
        if (data.getDeltaY() >= 0.0
                && data.getAirTicks() > limit
                && !data.getPlayer().isFlying()
                && data.liquidTicks() > 10
                && !data.isOnClimbableBlock()
                && data.getPlayer().getVelocity().getY() < -0.075
                && !data.isTakingVelocity()
                && data.getPlayer().getVehicle() == null
                && data.teleportTicks() > 20) {
            flag(data, "y motion higher than 0, m: " + data.getDeltaY() + ", " + data.getPlayer().getLocation().getBlock().getType().toString());
        }
    }
}
