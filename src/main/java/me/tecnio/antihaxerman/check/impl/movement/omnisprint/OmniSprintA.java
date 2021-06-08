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

package me.tecnio.antihaxerman.check.impl.movement.omnisprint;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;
import me.tecnio.antihaxerman.util.PlayerUtil;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@CheckInfo(name = "OmniSprint", type = "A", description = "Detects sprinting in a wrong direction.")
public final class OmniSprintA extends Check {

    Set<Integer> NO_SPRINT_DIRECTIONS = new HashSet<>(Arrays.asList(90, 135, 180));

    public OmniSprintA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isOnGround();
            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double moveAngle = MathUtil.getMoveAngle(data.getPositionProcessor().getLocation(),
                    data.getPositionProcessor().getLastLocation());

            final double yaw = data.getRotationProcessor().getYaw();
            final Vector direction = new Vector(-Math.sin(yaw * Math.PI / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(yaw * Math.PI / 180.0F) * (float) 1 * 0.5F);

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();

            final Vector move = new Vector(deltaX, 0.0, deltaZ);
            final double delta = move.distanceSquared(direction);

            boolean cantSprint = false;

            for(int angle : NO_SPRINT_DIRECTIONS) {

                if (Math.abs(moveAngle - angle) <= 10F) {
                    cantSprint = true;
                    break;
                }

            }

            final boolean exempt = isExempt(ExemptType.VELOCITY, ExemptType.CHUNK, ExemptType.UNDERBLOCK, ExemptType.ICE, ExemptType.LIQUID);
            final boolean invalid = delta > getLimit() && deltaXZ > 0.1 && sprinting && onGround && cantSprint;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                resetBuffer();
            }
        }
    }

    private double getLimit() {
        return data.getPlayer().getWalkSpeed() > 0.2f ? .23 * 1 +
                ((data.getPlayer().getWalkSpeed() / 0.2f) * 0.36) : 0.23 + (PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.SPEED) * 0.062f);
    }
}
