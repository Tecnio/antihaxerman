package me.tecnio.antihaxerman.check.impl.timer;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.math.MovingStats;

@CheckInfo(name = "Timer", type = "A")
public final class TimerA extends Check {
    public TimerA(PlayerData data) {
        super(data);
    }

    /*
     * Credits to Elevated https://github.com/ElevatedDev/Frequency
     */

    private long lastFlying = 0L;
    private final MovingStats movingStats = new MovingStats(20);

    @Override
    public void onFlying() {
        final long now = System.currentTimeMillis();

        final boolean exempt = data.teleportTicks() < 20 || PacketEvents.getAPI().getServerUtils().getTPS() < 18 || now - lastFlying < 1;

        sample: {
            if (exempt) break sample;

            movingStats.add(now - lastFlying);
        }

        analyze: {
            final double threshold = 7.07;
            final double deviation = movingStats.getStdDev(threshold);

            if (deviation >= threshold || Double.isNaN(deviation)) {
                resetBuffer();

                break analyze;
            }

            if (increaseBuffer() > 30) {
                flag();
            }
        }

        this.lastFlying = now;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.STEER_VEHICLE) {
            if (data.getPlayer().isInsideVehicle()) {
                resetBuffer();
            }
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketId() == PacketType.Server.POSITION) {
            resetBuffer();
        }
    }
}
