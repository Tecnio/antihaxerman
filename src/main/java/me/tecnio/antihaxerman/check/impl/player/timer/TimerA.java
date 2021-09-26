



package me.tecnio.antihaxerman.check.impl.player.timer;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MovingStats;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Timer", type = "A", description = "Detects game speed modifications.")
public final class TimerA extends Check {

    private final MovingStats movingStats = new MovingStats(20);

    private long lastFlying = 0L;
    private long allowance = 0;

    public TimerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                return;
            }
            final long now = now();

            final boolean exempt = this.isExempt(ExemptType.LAGGINGHARD, ExemptType.RESPAWN, ExemptType.TPS, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.VEHICLE);

            handle: {
                if (exempt) break handle;

                final long delay = now - lastFlying;
                if (delay < 1) break handle;

                movingStats.add(delay);

                final double threshold = 7.07;
                final double deviation = movingStats.getStdDev(threshold);

                if (deviation < threshold && !Double.isNaN(deviation)) {
                    allowance += 50;
                    allowance -= delay;

                    if (allowance > Math.ceil(threshold)) fail();
                } else {
                    allowance = 0;
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            movingStats.add(125L);
        }
    }
}
