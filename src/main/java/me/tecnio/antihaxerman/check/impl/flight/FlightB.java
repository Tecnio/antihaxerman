package me.tecnio.antihaxerman.check.impl.flight;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.CollisionUtils;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Flight", type = "B")
public final class FlightB extends Check {
    public FlightB(PlayerData data) {
        super(data);
    }

    @Override
    public void onFlying() {
        final double limit = 6 + (PlayerUtils.getPotionEffectLevel(data.getPlayer(), PotionEffectType.JUMP));

        final boolean exempt = data.getPlayer().isFlying() || data.pistonTicks() < 10 || data.liquidTicks() < 10 || data.isOnGround() || data.isNearBoat() || CollisionUtils.isOnGround(data) || data.climbableTicks() < 10 || data.getPlayer().getVelocity().getY() >= -0.075 || data.isTakingVelocity() || data.getPlayer().isInsideVehicle() || data.teleportTicks() < 20 || !data.getLocation().getBlock().getType().toString().equalsIgnoreCase("AIR");
        final boolean invalid = data.getDeltaY() > 0.0 && data.getAirTicks() > limit;

        if (invalid && !exempt) {
            if (increaseBuffer() > 2) {
                flag();
            }
        } else {
            decreaseBufferBy(0.1);
        }
    }
}
