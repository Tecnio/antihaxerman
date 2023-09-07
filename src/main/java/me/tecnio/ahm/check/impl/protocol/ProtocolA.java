package me.tecnio.ahm.check.impl.protocol;

import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.RotationCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.update.RotationUpdate;

@CheckManifest(name = "Protocol", type = "A", description = "Detects pitch over 90.")
public final class ProtocolA extends Check implements RotationCheck {

    public ProtocolA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final RotationUpdate update) {
        final float pitch = Math.abs(update.getPitch());

        if (pitch > 90.0F && !this.isExempt(ExemptType.TELEPORT)) {
            this.fail("p: " + pitch);
        }
    }
}
