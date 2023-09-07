package me.tecnio.ahm.check.impl.protocol;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientUseEntity;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;

@CheckManifest(name = "Protocol", type = "D", description = "Detects post use entity.")
public final class ProtocolD extends Check implements PacketCheck {

    private boolean sent = false;

    public long lastFlying, lastPacket;
    public double buffer = 0.0;

    public ProtocolD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying) {
            final long now = packet.getTimestamp();
            final long delay = now - this.lastPacket;

            if (this.sent) {
                if (delay > 40L && delay < 100L) {
                    this.buffer += 0.25;

                    if (this.buffer > 0.5) {
                        this.fail();
                    }
                } else {
                    this.buffer = Math.max(this.buffer - 0.025, 0);
                }

                this.sent = false;
            }

            this.lastFlying = now;
        } else if (packet instanceof PacketPlayClientUseEntity) {
            final long now = packet.getTimestamp();
            final long delay = now - this.lastFlying;

            if (delay < 10L) {
                this.lastPacket = now;
                this.sent = true;
            } else {
                this.buffer = Math.max(this.buffer - 0.025, 0.0);
            }
        }
    }
}
