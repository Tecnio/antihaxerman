package me.tecnio.antihaxerman.checks.impl.player.timer;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.checks.SetBackType;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packet.PacketType;

import java.util.Deque;
import java.util.LinkedList;

@CheckInfo(name = "Timer", type = "A")
public final class TimerA extends Check {

    private Deque<Long> flyingDeque = new LinkedList<>();
    private double lastDeviation;

    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())){
            flyingDeque.add(System.currentTimeMillis());

            if (flyingDeque.size() == 50) {
                final double deviation = MathUtils.getStandardDeviation(flyingDeque.stream().mapToLong(l -> l).toArray());

                if (deviation <= 710 && (Math.abs(deviation - lastDeviation) < 20)) {
                    if (++preVL > 1)
                        flag(data, "deviation = " + deviation, SetBackType.BACK);
                } else preVL = 0;
                this.lastDeviation = deviation;
                flyingDeque.clear();
            }
        }
    }
}
