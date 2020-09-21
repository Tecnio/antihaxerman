package me.tecnio.antihaxerman.checks.impl.player.timer;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

import java.util.ArrayDeque;

@CheckInfo(name = "Timer", type = "B")
public final class TimerB extends Check {
    public TimerB(PlayerData data) {
        super(data);
    }

    /*
     * Skidded from https://github.com/GladUrBad/Medusa/
     */

    private long lastTime;
    private ArrayDeque<Long> samples = new ArrayDeque<>();

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (isFlyingPacket(e)) {
            final long time = time();
            final long delay = time - lastTime;

            samples.add(delay);
            if (samples.size() >= 20) {
                double timerAverage = samples.parallelStream().mapToDouble(value -> value).average().orElse(0.0D);
                double timerSpeed = 50 / timerAverage;

                if (timerSpeed > 1.075 || timerSpeed < 0.925) {
                    flag(data, "gamespeed changed. gs: " + timerSpeed);
                } else preVL = 0;

                samples.clear();
            }
            lastTime = time;
        } else if (e.getPacketId() == PacketType.Client.STEER_VEHICLE) {
            samples.clear();
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent e) {
        if (e.getPacketId() == PacketType.Server.POSITION) {
            samples.clear();
        }
    }
}
