

package me.tecnio.antihaxerman.check.impl.player.timer;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MovingStats;

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
            final long now = now();
            final int serverTicks = AntiHaxerman.INSTANCE.getTickManager().getTicks();
            
            final boolean exempt = this.isExempt(ExemptType.TPS, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.LAGGING, ExemptType.VEHICLE);
            final boolean accepted = data.getConnectionProcessor().getKeepAliveTime(serverTicks).isPresent();

            handle: {
                if (exempt || !accepted) break handle;

                final long delay = now - lastFlying;
                movingStats.add(delay);
                
                final double threshold = 7.07;
                final double deviation = movingStats.getStdDev(threshold);

                if (deviation < threshold && !Double.isNaN(deviation)) {
                    allowance += 50;
                    allowance -= delay;

                    if (allowance > Math.floor(threshold)) fail();
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
