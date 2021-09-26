

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

@CheckInfo(name = "Timer", type = "C", description = "Checks for game speed changes.")
public final class TimerC extends Check {

    private final EvictingList<Long> samples = new EvictingList<>(50);
    private long lastFlying;

    public TimerC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying() && !isExempt(ExemptType.LONG_JOINED, ExemptType.AFK, ExemptType.TPS)) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_17)) {
                return;
            }
            final long now = now();
            final long delta = now - lastFlying;

            if (delta > 0) {
                samples.add(delta);
            }

            if (samples.isFull()) {
                final double average = MathUtil.getAverage(samples);
                final double speed = 50 / average;

                if (speed >= 1.025) {
                    if (increaseBuffer() > 30) {
                        fail();
                    }
                } else {
                    decreaseBuffer();
                }
            }

            lastFlying = now;
        } else if (packet.isTeleport()) {
            samples.add(135L);
        }
    }
}
