

package me.tecnio.antihaxerman.check.impl.player.timer;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Timer", type = "D", description = "Uses a balance/allowance system to flag game speed changes.")
public final class TimerD extends Check {

    private long balance = 0L;
    private long lastFlying = 0L;

    public TimerD(final PlayerData data) {
        super(data);
    }


    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final long now = packet.getTimeStamp();
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                return;
            }
            handle: {
                if (isExempt(ExemptType.TPS, ExemptType.JOINED)) break handle;
                if (lastFlying == 0L) break handle;

                final long delay = now - lastFlying;

                balance += 50L - delay;

                if (balance > 5L) {
                    if (increaseBuffer() > 5) {
                        fail("balance: " + balance);
                    }

                    balance = 0;
                } else {
                    decreaseBufferBy(0.001);
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            if (isExempt(ExemptType.TPS, ExemptType.JOINED)) return;
            if (lastFlying == 0L) return;

            balance -= 50L;
        }
    }
}
