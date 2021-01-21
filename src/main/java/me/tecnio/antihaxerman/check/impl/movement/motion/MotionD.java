/*
 *  Copyright (C) 2020 Tecnio
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

@CheckInfo(name = "Motion", type = "D", description = "Checks for invalid vertical acceleration.")
public final class MotionD extends Check {
    public MotionD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final double modifier = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1;
            final double maximum = 0.6 + modifier;

            final boolean exempt = isExempt(ExemptType.PISTON, ExemptType.LIQUID, ExemptType.FLYING, ExemptType.WEB, ExemptType.TELEPORT, ExemptType.SLIME, ExemptType.CHUNK);

            final boolean invalidNoVelocity = deltaY > maximum && !isExempt(ExemptType.VELOCITY);
            final boolean invalidVelocity = deltaY > (maximum + data.getVelocityProcessor().getVelocityY()) && isExempt(ExemptType.VELOCITY);

            final boolean invalid = invalidNoVelocity || invalidVelocity;

            if (invalid && !exempt) fail();
        }
    }
}
