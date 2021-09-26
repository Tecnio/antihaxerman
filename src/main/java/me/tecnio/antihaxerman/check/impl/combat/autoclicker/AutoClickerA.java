

package me.tecnio.antihaxerman.check.impl.combat.autoclicker;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "AutoClicker", type = "A", description = "Detects high amounts of clicks in a second.")
public final class AutoClickerA extends Check {
    public AutoClickerA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isArmAnimation()) {
            final double cps = data.getClickProcessor().getCps();

            final boolean exempt = isExempt(ExemptType.DROP, ExemptType.AUTOCLICKER);
            final boolean invalid = cps > 60 && !Double.isInfinite(cps) && !Double.isNaN(cps);

            if (invalid && !exempt) {
                fail("CPS=" + cps);
            }
        }
    }
}
