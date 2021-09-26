

package me.tecnio.antihaxerman.check.impl.player.timer;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;
import me.tecnio.antihaxerman.util.type.EvictingList;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@CheckInfo(name = "Timer", type = "B", description = "Checks packet delay between packets.")
public final class TimerB extends Check {

    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlying;

    public TimerB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                return;
            }
            final long now = now();

            final boolean exempt = this.isExempt(ExemptType.TPS, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.VEHICLE);

            handle: {
                if (exempt) break handle;

                final long delay = now - lastFlying;

                if (delay > 0) {
                    samples.add(delay);
                }

                if (samples.isFull()) {
                    final double average = MathUtil.getAverage(samples);
                    final double deviation = MathUtil.getStandardDeviation(samples);

                    final double speed = 50.0 / average;

                    final boolean invalid = deviation < 40.0 && speed < 0.6 && !Double.isNaN(deviation);

                    if (invalid) {
                        if (increaseBuffer() > 30) {
                            fail("Speed: " + speed);
                            multiplyBuffer(0.50);
                        }
                    } else {
                        decreaseBufferBy(10);
                    }
                }
            }

            this.lastFlying = now;
        } else if (packet.isTeleport()) {
            samples.add(125L);
        }
    }
}
