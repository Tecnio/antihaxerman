package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

@CheckInfo(name = "Aim", type = "I", description = "Checks for a valid sensitivity in the rotation packet.")
public class AimI extends Check {

    public AimI(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isRotation()) {
            final float deltaPitch = this.data.getRotationProcessor().getDeltaPitch();
            final float lastDeltaPitch = this.data.getRotationProcessor().getLastDeltaPitch();
            final long expandedDeltaPitch = (long)(deltaPitch * MathUtil.EXPANDER);
            final long expandedLastDeltaPitch = (long)(lastDeltaPitch * MathUtil.EXPANDER);
            final long gcd = MathUtil.getGcd(expandedDeltaPitch, expandedLastDeltaPitch);
            final boolean exempt = deltaPitch == 0.0f || lastDeltaPitch == 0.0f || this.isExempt(ExemptType.CINEMATIC_TIME, ExemptType.CINEMATIC);
            if (!exempt && gcd < 131072L) {
                if (increaseBuffer() > 20.0) {
                    fail("gcd=" + gcd);
                }
            }
            else {
                setBuffer(Math.max(0.0, getBuffer() - 2.0));
            }
        }
    }
}
