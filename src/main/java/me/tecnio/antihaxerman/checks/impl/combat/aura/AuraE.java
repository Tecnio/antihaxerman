package me.tecnio.antihaxerman.checks.impl.combat.aura;

import com.google.common.collect.Lists;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.MathUtils;

import java.util.Deque;
import java.util.List;

@CheckInfo(name = "Aura", type = "E")
public class AuraE extends Check {

    private int hitTicks;
    private Deque<Float> pitchSamples = Lists.newLinkedList();
    private Deque<Double> samples = Lists.newLinkedList();

    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if(e.getPacketId() == PacketType.Client.LOOK || e.getPacketId() == PacketType.Client.POSITION_LOOK) {
            if (++hitTicks < 2) {
                // Get the deltas from the rotation update
                final float deltaYaw = data.getDeltaYaw();
                final float deltaPitch = data.getDeltaPitch();

                final float lastDeltaYaw = data.getLastDeltaYaw();
                final float lastDeltaPitch = data.getLastDeltaPitch();

                // Grab the gcd using an expander.
                final double divisorYaw = MathUtils.getGcd((long) (deltaYaw * MathUtils.EXPANDER), (long) (lastDeltaYaw * MathUtils.EXPANDER));
                final double divisorPitch = MathUtils.getGcd((long) (deltaPitch * MathUtils.EXPANDER), (long) (lastDeltaPitch * MathUtils.EXPANDER));

                // Get the constant for both rotation updates by dividing by the expander
                final double constantYaw = divisorYaw / MathUtils.EXPANDER;
                final double constantPitch = divisorPitch / MathUtils.EXPANDER;

                // Get the estimated mouse delta from the constant
                final double currentX = deltaYaw / constantYaw;
                final double currentY = deltaPitch / constantPitch;

                // Get the estimated mouse delta from the old rotations using the new constant
                final double previousX = lastDeltaYaw / constantYaw;
                final double previousY = lastDeltaPitch / constantPitch;

                // Make sure the rotation is not very large and not equal to zero and get the modulo of the xys
                if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f) {
                    final double moduloX = currentX % previousX;
                    final double moduloY = currentY % previousY;

                    // Get the floor delta of the the moduloes
                    final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                    final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                    samples.add(floorModuloX);
                    pitchSamples.add(deltaPitch);
                    if(samples.size() > 20 && pitchSamples.size() > 20) {
                        double deviation = MathUtils.getStandardDeviation((List<Double>) samples);
                        int duplicates = (int) (samples.size() - samples.parallelStream().distinct().count());
                        int pitchDuplicates = (int) (pitchSamples.size() - pitchSamples.parallelStream().distinct().count());
                        int combinedDuplicates = duplicates + pitchDuplicates;
                        if(Double.isNaN(deviation) && combinedDuplicates <= 7) {
                            if (++preVL > 2) {
                                flag(data, "LiquidBounce Killaura (or similar)");
                            }
                        }else preVL = 0;
                        samples.clear();
                        pitchSamples.clear();
                    }


                }
            }
        } else if(e.getPacketId() == PacketType.Client.USE_ENTITY) {
            hitTicks = 0;
        }
    }
}
