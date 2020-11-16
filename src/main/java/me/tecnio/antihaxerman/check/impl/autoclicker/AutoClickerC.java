package me.tecnio.antihaxerman.check.impl.autoclicker;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.math.MathUtils;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

import java.util.Deque;
import java.util.LinkedList;

@CheckInfo(name = "AutoClicker", type = "C")
public final class AutoClickerC extends Check {
    public AutoClickerC(PlayerData data) {
        super(data);
    }

    private final Deque<Double> delays = new LinkedList<>();
    private int ticks;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.ARM_ANIMATION) {
            if (!data.isDigging()) {
                if (ticks < 10) {
                    delays.add((double) ticks);

                    if (delays.size() >= 120) {
                        final double kurtosis = MathUtils.getKurtosis(delays);

                        if (Double.isNaN(kurtosis)) {
                            if (increaseBuffer() > 1) {
                                flag();
                            }
                        } else resetBuffer();

                        delays.clear();
                    }
                }
                ticks = 0;
            } else {
                resetBuffer();
            }
        } else if (PacketUtils.isFlyingPacket(event.getPacketId())) {
            ticks++;
        }
    }
}
