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

package me.tecnio.antihaxerman.check.impl.combat.velocity;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@CheckInfo(name = "Velocity", type = "B", description = "Checks for horizontal velocity modifications.")
public final class VelocityB extends Check {

    double kbX, kbZ;

    public VelocityB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {

            final int ticksSinceVelocity = data.getVelocityProcessor().getTakingVelocityTicks();

            if(ticksSinceVelocity == 1) {
                kbX = data.getVelocityProcessor().getVelocityX();
                kbZ = data.getVelocityProcessor().getVelocityZ();
            }

            if(hitTicks() <= 1 && data.getActionProcessor().isSprinting()) {
                kbX *= 0.6D;
                kbZ *= 0.6D;
            }

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double velocityXZ = Math.hypot(kbX, kbZ);

            final double diffH = Math.max((deltaXZ / velocityXZ), (lDeltaXZ / velocityXZ));

            final double percentage = diffH * 100.0;

            final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.PISTON, ExemptType.CLIMBABLE,
                    ExemptType.UNDERBLOCK, ExemptType.NEAR_WALL, ExemptType.TELEPORT, ExemptType.FLYING);

            final boolean invalid = percentage < 70.0;

            if(kbX != 0 || kbZ != 0) {

                if (invalid && !exempt) {
                    if (increaseBuffer() > 4) {
                        fail(String.format("(Horizontal) Velocity: ~%.2f%%, Tick: %d", percentage, ticksSinceVelocity));
                    }
                    resetState();
                } else {
                    decreaseBuffer();
                }
                debug(String.format("diffH: %.2f lDiffH: %.2f tick: %d buffer: %.2f",
                        (deltaXZ / velocityXZ), (lDeltaXZ / velocityXZ), ticksSinceVelocity, getBuffer()));
            }

            final double x = data.getPositionProcessor().getX();
            final double y = data.getPositionProcessor().getY();
            final double z = data.getPositionProcessor().getZ();

            final Location blockLocation = new Location(data.getPlayer().getWorld(), x, Math.floor(y) - 1, z);

            final double friction = BlockUtil.getBlockFriction(blockLocation) * 0.91F;

            kbX *= friction;
            kbZ *= friction;

            if(Math.abs(kbX) < 0.005 || Math.abs(kbZ) < 0.005) {
                resetState();
            }

            if(ticksSinceVelocity >= 2) {
                resetState();
            }
        }
    }

    public void resetState() {
        kbX = 0;
        kbZ = 0;
    }
}
