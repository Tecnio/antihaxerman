package me.tecnio.antihaxerman.check.impl.autoclicker;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.data.EvictingList;
import me.tecnio.antihaxerman.utils.math.MathUtils;

@CheckInfo(name = "AutoClicker", type = "B")
public final class AutoClickerB extends Check {
    public AutoClickerB(PlayerData data) {
        super(data);
    }

    private final EvictingList<Long> ticks = new EvictingList<>(30);
    private double lastDeviation;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.ARM_ANIMATION) {
            if (!data.isDigging()) ticks.add((long) (data.getTick() * 50.0));
            else resetBuffer();

            if (ticks.size() >= 30) {
                final double deviation = MathUtils.stdDev(ticks);

                final double diff = Math.abs(deviation - lastDeviation);

                if (diff < 6) {
                    if (increaseBuffer() > 3) {
                        flag();
                    }
                } else {
                    decreaseBuffer();
                }

                lastDeviation = deviation;
            }
        }
    }
}
