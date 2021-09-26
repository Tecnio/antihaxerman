

package me.tecnio.antihaxerman.check.impl.combat.autoclicker;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

import java.util.ArrayDeque;
import java.util.Deque;

@CheckInfo(name = "AutoClicker", type = "C", description = "Checks if the deviation is too low.")
public final class AutoClickerC extends Check {

    private final Deque<Long> samples = new ArrayDeque<>();
    private int ticks;

    public AutoClickerC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation() && !isExempt(ExemptType.DROP, ExemptType.AUTOCLICKER)) {
            if (ticks > 50) samples.clear();
            else samples.add(ticks * 50L);

            if (samples.size() == 50) {
                final double deviation = MathUtil.getStandardDeviation(samples);

                if (deviation < 150) {
                    if (increaseBuffer() > 2) {
                        fail();
                    }
                } else {
                    decreaseBufferBy(0.25);
                }

                samples.clear();
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ++ticks;
        }
    }
}
