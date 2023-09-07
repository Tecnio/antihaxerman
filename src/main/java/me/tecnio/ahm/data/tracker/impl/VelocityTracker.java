package me.tecnio.ahm.data.tracker.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.server.PacketPlayServerEntityVelocity;
import ac.artemis.packet.wrapper.server.PacketPlayServerExplosion;
import lombok.Getter;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.Tracker;
import me.tecnio.ahm.util.type.Motion;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public final class VelocityTracker extends Tracker {

    // TODO: 8/27/23 use tick timers I forgot to use themn

    private Vector velocity = new Vector(), lastVelocity = new Vector();
    private int ticks, ticksSinceVelocity, maxVelocityTicks, velocityTicks;

    private boolean lastTickVelocity;

    private Vector explosion = new Vector();
    private int ticksSinceExplosion, maxExplosionTicks, explosionTicks;

    private final List<Consumer<Motion>> actions = new ArrayList<>();

    public VelocityTracker(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayServerEntityVelocity) {
            final PacketPlayServerEntityVelocity wrapper = ((PacketPlayServerEntityVelocity) packet);

            if (wrapper.getEntityId() == data.getPlayer().getEntityId()) {
                final Vector velocity = new Vector(wrapper.getX() / 8000.0D, wrapper.getY() / 8000.0D, wrapper.getZ() / 8000.0D);

                data.getConnectionTracker().confirm(() -> {
                    this.velocity = velocity;
                    this.ticksSinceVelocity = 0;

                    this.velocityTicks = this.ticks;
                    this.maxVelocityTicks = (int) (((velocity.getX() + velocity.getY() + velocity.getZ()) / 2 + 2) * 10);

                    this.actions.add(motion -> motion.set(velocity));
                });
            }
        } else if (packet instanceof PacketPlayServerExplosion) {
            final PacketPlayServerExplosion wrapper = ((PacketPlayServerExplosion) packet);

            final Vector explosion = new Vector(wrapper.getMotionX(), wrapper.getMotionY(), wrapper.getMotionZ());

            data.getConnectionTracker().confirm(() -> {
                this.explosion = explosion;
                this.ticksSinceExplosion = 0;

                this.explosionTicks = this.ticks;
                this.maxExplosionTicks = (int) (((explosion.getX() + explosion.getY() + explosion.getZ()) / 2 + 2) * 10);

                this.actions.add(motion -> motion.add(explosion));
            });
        } else if (packet instanceof PacketPlayClientFlying) {
            ++this.ticks;
            ++this.ticksSinceVelocity;
            ++this.ticksSinceExplosion;
        }
    }

    @Override
    public void handlePost(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            this.actions.clear();

            this.lastVelocity = this.velocity;
            this.lastTickVelocity = this.ticksSinceExplosion == 1 || this.ticksSinceVelocity == 1;
        }
    }

    public boolean isTakingVelocity() {
        return Math.abs(this.ticks - this.velocityTicks) < this.maxVelocityTicks
                || Math.abs(this.ticks - this.explosionTicks) < this.maxExplosionTicks;
    }

    public Vector cloneVector(final Vector vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }
}
