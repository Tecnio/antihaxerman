package me.tecnio.ahm.check.impl.timer;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.server.PacketPlayServerPosition;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

/**
 * Check to detect increased game tick-rate using a balance system.
 */
@CheckManifest(name = "Timer", type = "A", description = "Uses a balance system to check for increased game tick-rate.")
public final class TimerA extends Check implements PacketCheck {

    private long allowance = -50L, lastFlying;

    public TimerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            handle: {
                if (data.getTicks() < 200 || this.lastFlying == 0L) break handle;

                final long delay = packet.getTimestamp() - this.lastFlying;

                // Update allowance using balance system
                this.allowance += 50L;
                this.allowance -= delay;

                // It's your job to figure out a way to fix this.
                // The grim developer (https://github.com/MWHunter) has made a nice little concept.
                // https://i.imgur.com/Hk2Wb6c.png
                if (this.allowance < -5000L) data.haram();

                // Check if allowance is positive
                if (this.allowance > 0L) {
                    if (this.buffer.increase() > 2) {
                        this.fail("a: " + this.allowance);
                    }

                    this.allowance = -50L;
                } else {
                    this.buffer.decreaseBy(0.001D);
                }
            }

            this.lastFlying = packet.getTimestamp();
        }

        else if (packet instanceof PacketPlayServerPosition) {
            this.allowance -= 50L;
        }
    }
}
