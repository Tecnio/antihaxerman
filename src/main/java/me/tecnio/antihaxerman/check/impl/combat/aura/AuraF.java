package me.tecnio.antihaxerman.check.impl.combat.aura;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;
import me.tecnio.antihaxerman.util.type.EvictingList;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Aura", type = "F", description = "Checks for invalid rotation.")
public class AuraF extends Check {

    private final EvictingList<Double> differenceSamples = new EvictingList<>(25);

    public AuraF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation() && isExempt(ExemptType.COMBAT)) {
            final Player player = data.getPlayer();
            final Entity target = data.getCombatProcessor().getTarget();

            if (target != null) {
                final Location origin = player.getLocation().clone();
                final Vector end = target.getLocation().clone().toVector();

                final float optimalYaw = origin.setDirection(end.subtract(origin.toVector())).getYaw() % 360F;
                final float rotationYaw = data.getRotationProcessor().getYaw();
                final float deltaYaw = data.getRotationProcessor().getDeltaYaw();
                final float fixedRotYaw = (rotationYaw % 360F + 360F) % 360F;

                final double difference = Math.abs(fixedRotYaw - optimalYaw);

                if (deltaYaw > 3f) {
                    differenceSamples.add(difference);
                }
                if (differenceSamples.isFull()) {
                    final double average = MathUtil.getAverage(differenceSamples);
                    final double deviation = MathUtil.getStandardDeviation(differenceSamples);

                    final boolean invalid = average < 7 && deviation < 12;

                    if (invalid) {
                        if (increaseBuffer() > 30) {
                            fail(String.format("dev=%.2f, avg=%.2f, buf=%.2f", deviation, average, getBuffer()));
                        }
                    } else {
                        setBuffer(getBuffer() > 0 ? 1 : 0);
                    }

                }
            }
        }
    }
}
