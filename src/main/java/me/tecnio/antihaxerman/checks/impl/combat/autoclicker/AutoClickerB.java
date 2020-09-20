package me.tecnio.antihaxerman.checks.impl.combat.autoclicker;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.MathUtils;

import java.util.Deque;
import java.util.LinkedList;

@CheckInfo(name = "AutoClicker", type = "B")
public final class AutoClickerB extends Check {
    public AutoClickerB(PlayerData data) {
        super(data);
    }

    private final Deque<Long> ticks = new LinkedList<>();
    private double lastDeviation;

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (e.getPacketId() == PacketType.Client.ARM_ANIMATION){
            if (!data.isDigging()) ticks.add((long) (data.getTicks() * 50.0));
            if (ticks.size() >= 10) {
                final double deviation = MathUtils.getStandardDeviation(ticks.stream().mapToLong(d -> d).toArray());

                final double diff = Math.abs(deviation - lastDeviation);

                if (diff < 10) {
                    if (++preVL > 5) {
                        flag(data, "low deviation difference, d: " + diff);
                    }
                } else preVL *= 0.75;

                ticks.clear();
                lastDeviation = deviation;
            }
        }
    }
}
