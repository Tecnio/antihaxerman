package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "Aim", type = "L", description = "Checks if pitch gcd is lower than the lowest possible on yawn.")
public class AimL extends Check {

    private Float lastYawChange;
    private Float lastPitchChange;

    public AimL(PlayerData data) {
        super(data);
    }


    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (data.getCombatProcessor().getHitTicks() > 10)
                return;

            WrappedPacketInFlying wrapped= new WrappedPacketInFlying(packet.getRawPacket());

            float yawChange = Math.abs(wrapped.getYaw() - data.getRotationProcessor().getLastYaw());
            float pitchChange = Math.abs(wrapped.getPitch() - data.getRotationProcessor().getLastPitch());

            if (lastYawChange != null && lastPitchChange != null && data.getPositionProcessor().getDeltaXZ() > 0) {
                float yawAccel = Math.abs(lastYawChange - yawChange);
                float pitchAccel = Math.abs(lastPitchChange - pitchChange);

                if (yawChange > 3F && pitchChange < 10F && yawAccel > 2F && pitchAccel > 2F && pitchChange < yawChange) {
                    double pitchGcd = gcd(pitchChange, lastPitchChange);

                    if (pitchGcd < 0.009) {
                        if (increaseBuffer() > 10) {
                            fail("PitchGcd: " + pitchGcd);
                        }
                    } else {
                        decreaseBufferBy(0.1);
                    }
                } else {
                    decreaseBufferBy(0.3);
                }
            }

            this.lastYawChange = yawChange;
            this.lastPitchChange = pitchChange;
        }
    }

    public double gcd(double a, double b) {
        if (a < b)
            return gcd(b, a);
        else if (Math.abs(b) < 0.001) // base case
            return a;
        else
            return gcd(b, a - Math.floor(a / b) * b);
    }
}
