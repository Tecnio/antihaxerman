package me.tecnio.antihaxerman.checks.impl.player.timer;

import me.tecnio.antihaxerman.Config;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

import java.util.Deque;
import java.util.LinkedList;

@CheckInfo(name = "Timer", type = "A")
public final class TimerA extends Check {
    public TimerA(PlayerData data) {
        super(data);
    }

    private Deque<Long> flyingDeque = new LinkedList<>();
    private double lastDeviation;

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())){
            flyingDeque.add(System.currentTimeMillis());

            if (flyingDeque.size() == 50) {
                final double deviation = MathUtils.getStandardDeviation(flyingDeque.stream().mapToLong(l -> l).toArray());

                if (deviation <= Config.TIMER_DEVIATION && (Math.abs(deviation - lastDeviation) < Config.TIMER_DEVIATION_DIFF)) {
                    if (++preVL > 1) flag(data, "deviation = " + deviation);
                } else preVL = 0;
                this.lastDeviation = deviation;
                flyingDeque.clear();
            }
        }
    }
}
