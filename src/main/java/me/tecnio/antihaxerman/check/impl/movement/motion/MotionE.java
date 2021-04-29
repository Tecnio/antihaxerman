/*
 *  Copyright (C) 2020 - 2021 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.check.impl.movement.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "E", description = "Checks for invalid vertical acceleration.")
public final class MotionE extends Check {
    public MotionE(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = Math.abs(data.getPositionProcessor().getDeltaY());
            final double lastDeltaY = Math.abs(data.getPositionProcessor().getLastDeltaY());

            final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
            final double modifierVelocity = isExempt(ExemptType.VELOCITY) ? Math.abs(data.getVelocityProcessor().getVelocityY()) : 0.0;

            final double limit = 1.0 + modifierJump + modifierVelocity;

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.PISTON,
                    ExemptType.VEHICLE, ExemptType.BOAT, ExemptType.VEHICLE,
                    ExemptType.SLIME, ExemptType.CHUNK, ExemptType.FLYING);
            final boolean invalid = deltaY > limit && lastDeltaY < 0.2;

            if (invalid && !exempt) fail();
        }
    }
}
