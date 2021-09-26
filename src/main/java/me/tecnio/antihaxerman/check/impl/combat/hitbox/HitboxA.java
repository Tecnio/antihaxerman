package me.tecnio.antihaxerman.check.impl.combat.hitbox;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import me.tecnio.antihaxerman.util.type.BoundingBox;
import me.tecnio.antihaxerman.util.type.RayTrace;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.NumberConversions;

@CheckInfo(name = "Hitbox", type = "A", description = "Checks for the angle of the attack.")
public class HitboxA extends Check {

    public HitboxA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();


            if (wrapper.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK
                    || data.getPlayer().getGameMode() != GameMode.SURVIVAL
                    || !(target instanceof Player || target instanceof Villager)
                    || target != lastTarget
                    || !data.getTargetLocations().isFull()) {
                return;
            }

            final int ticks = AntiHaxerman.INSTANCE.getTickManager().getTicks();
            final int pingTicks = NumberConversions.floor(PlayerUtil.getPing(data.getPlayer()) / 50.0) + 3;

            final RayTrace rayTrace = new RayTrace(data.getPlayer());

            final int collided = (int) data.getTargetLocations().stream()
                    .filter(pair -> Math.abs(ticks - pair.getY() - pingTicks) < 3)
                    .filter(pair -> {
                        final Location location = pair.getX();
                        final BoundingBox boundingBox = new BoundingBox(
                                location.getX() - 0.4,
                                location.getX() + 0.4,
                                location.getY(),
                                location.getY() + 1.9,
                                location.getZ() - 0.4,
                                location.getZ() + 0.4
                        );

                        return boundingBox.collidesD(rayTrace, 0, 6) != 10;
                    }).count();

            final double sensitivity = 2;

            if(isExempt(ExemptType.LAGGING, ExemptType.LAGGINGHARD)) {
                return;
            }
            if (collided <= sensitivity && collided != sensitivity) {
                if (increaseBuffer() > 10) {
                    fail("collided=" + collided);
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
