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

@CheckInfo(name = "Motion", type = "A", description = "Checks for invalid jump motion.")
public final class MotionA extends Check {

    public MotionA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isOnGround();

            final double deltaY = data.getPositionProcessor().getDeltaY();

            final double y = data.getPositionProcessor().getY();
            final double lastY = data.getPositionProcessor().getLastY();

            final boolean step = y % 0.015625 == 0.0 && lastY % 0.015625 == 0.0;

            final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
            final double expectedJumpMotion = 0.42F + modifierJump;

            final boolean exempt = isExempt(ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.PISTON,
                    ExemptType.LIQUID, ExemptType.TELEPORT, ExemptType.WEB, ExemptType.BOAT, ExemptType.FLYING, ExemptType.SLIME,
                    ExemptType.UNDERBLOCK, ExemptType.CHUNK) || data.getPositionProcessor().getSinceBlockNearHeadTicks() < 5;
            final boolean invalid = deltaY != expectedJumpMotion && deltaY > 0.0 && !onGround && lastY % 0.015625 == 0.0 && !step;

            if (invalid && !exempt) fail(deltaY + " vel: " + isExempt(ExemptType.VELOCITY));
            if (step && deltaY > 0.6F && !exempt) fail();
        }
    }
}
