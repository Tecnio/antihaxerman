package me.tecnio.antihaxerman.check.impl.reach;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach", type = "A", autoBan = false)
public final class ReachA extends Check {
    public ReachA(PlayerData data) {
        super(data);
    }

    private boolean attacked;

    @Override
    public void onFlying() {
        if (!attacked) return;
        attacked = false;

        final Entity target = data.getTarget();

        if (!(target instanceof Player)) return;
        if (data.getTargetLocations().size() < 30) return;

        final int now = AntiHaxerman.getInstance().getTickProcessor().getTicks();
        final int latencyInTicks = (int) Math.floor(data.getTransactionPing() / 50.0);

        final Vector origin = data.getLocation().toVector().setY(0.0);

        final double maxDistance = data.getPlayer().getGameMode() == GameMode.CREATIVE ? 6.0 : 3.0;
        final double distance = data.getTargetLocations().stream()
                .filter(pair -> Math.abs(now - pair.getY() - latencyInTicks) < 2)
                .mapToDouble(pair -> {
                    final Vector targetLocation = pair.getX().toVector().setY(0.0);

                    return origin.distance(targetLocation) - 0.565686;
                })
                .min().orElse(-1);

        if (distance > maxDistance) {
            if (increaseBuffer() > 4) {
                flag("distance: " + distance);
            }
        } else {
            if (data.isLagging()) {
                decreaseBufferBy(0.1);
            } else {
                decreaseBufferBy(0.05);
            }
        }
    }

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        attacked = true;
    }
}
