package me.tecnio.antihaxerman.check.impl.combat.reach;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import me.tecnio.antihaxerman.util.type.HitboxExpansion;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach", type = "B", description = "Checks if player is attacking from a distance that's not possible.")
public class ReachB extends Check {

    public ReachB(PlayerData data) {
        super(data);
    }

    public int hits;

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if(data.getCombatProcessor().getHitTicks() > 3) {
                hits = 0;
            }
        }
        if (packet.isUseEntity()) {
            hits++;
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();

            if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK
                    || data.getPlayer().getGameMode() != GameMode.SURVIVAL
                    || !(target instanceof LivingEntity)
                    || target != lastTarget
                    || !data.getTargetLocations().isFull()
            ) return;

            final int ticks = AntiHaxerman.INSTANCE.getTickManager().getTicks();
            final int pingTicks = NumberConversions.floor(PlayerUtil.getPing(data.getPlayer()) / 50.0) + 4;

            final Vector player = data.getPlayer().getLocation().toVector().setY(0);

            final double distance = data.getTargetLocations().stream()
                    .filter(pair -> Math.abs(ticks - pair.getY() - pingTicks) < 3)
                    .mapToDouble(pair -> {
                        final Vector victim = pair.getX().toVector().setY(0);
                        final double expansion = HitboxExpansion.getExpansion(target);
                        return player.distance(victim) - expansion;
                    }).min().orElse(0);

            if (distance == 0) return;

            if(hits == 1 && distance < 3.1 && distance > 3.0) {
                return;
            }

            if(distance > 3.0) {
                if(increaseBufferBy(0.90) > 2) {
                    fail("Reach: " + distance);
                    resetBuffer();
                }
            }
            else {
                decreaseBufferBy(0.03);
            }
        }
    }
}
