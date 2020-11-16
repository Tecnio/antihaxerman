package me.tecnio.antihaxerman.check.impl.angle;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@CheckInfo(name = "Angle", type = "A")
public final class AngleA extends Check {
    public AngleA(PlayerData data) {
        super(data);
    }

    private boolean attacked;

    @Override
    public void onFlying() {
        if (!attacked) return;
        attacked = false;

        final Entity target = data.getTarget();

        if (!(target instanceof LivingEntity)) return;
        if (data.getTargetLocations().size() < 30) return;

        final int now = AntiHaxerman.getInstance().getTickProcessor().getTicks();
        final int latencyInTicks = (int) Math.floor(data.getTransactionPing() / 50.0);

        final Vector origin = data.getPlayer().getEyeLocation().toVector().setY(0.0);

        final float angle = (float) data.getTargetLocations().stream()
                .filter(pair -> Math.abs(now - pair.getY() - latencyInTicks) < 3)
                .mapToDouble(pair -> {
                    final Vector targetLocation = pair.getX().toVector();

                    final Vector dirToDestination = targetLocation.clone().setY(0.0).subtract(origin);
                    final Vector playerDirection = data.getPlayer().getEyeLocation().getDirection().setY(0.0);

                    return dirToDestination.angle(playerDirection);
                })
                .min().orElse(-1);

        final boolean exempt = origin.distance(target.getLocation().toVector()) < 1.2;

        if (angle > 0.6 && !exempt) {
            if (increaseBuffer() > 5) {
                flag();
            }
        } else {
            resetBuffer();
        }
    }

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        attacked = true;
    }
}
