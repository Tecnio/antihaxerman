

package me.tecnio.antihaxerman.check.impl.player.post;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Post", type = "G", description = "Checks for packet order for the packet 'WINDOW_CLICK'.")
public final class PostG extends Check {

    private boolean sent;
    public long lastFlying, lastPacket;

    public PostG(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long now = System.currentTimeMillis();
            final long delay = now - lastPacket;

            if (sent) {
                if (delay > 40L && delay < 100L) {
                    increaseBufferBy(0.25);

                    if (getBuffer() > 8) {
                        fail();
                    }
                } else {
                    decreaseBufferBy(0.025);
                }

                sent = false;
            }

            this.lastFlying = now;
        } else if (packet.isWindowClick()){
            final long now = System.currentTimeMillis();
            final long delay = now - lastFlying;

            if (delay < 10L) {
                lastPacket = now;
                sent = true;
            } else {
                decreaseBufferBy(0.0025);
            }
        }
    }
}
