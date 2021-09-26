

package me.tecnio.antihaxerman.check.impl.player.fastplace;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

import java.util.LinkedList;
import java.util.Queue;

@CheckInfo(name = "FastPlace", type = "A", description = "Checks if player placing blocks too fast.")
public final class FastPlaceA extends Check {

    private final Queue<Integer> delays = new LinkedList<>();

    private int movements;


    public FastPlaceA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if(packet.isBlockPlace() && !isBridging()) {
            if (movements < 10) {
                if (delays.add(movements) && delays.size() == 35) {
                    double avg = MathUtil.getAverage(delays);
                    double stDev = MathUtil.getStandardDeviation(delays);

                    if (avg < 4 && stDev < 0.15) {
                        fail();
                    }

                    delays.clear();
                }
            }

            movements = 0;
        }
        if(packet.isFlying() && !isBridging()) {
            ++movements;
        }
    }
}
