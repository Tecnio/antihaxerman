/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

package me.tecnio.antihaxerman.check.impl.speed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.world.BlockUtils;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Speed", type = "A")
public final class SpeedA extends Check {
    public SpeedA(PlayerData data) {
        super(data);
    }

    /*
     * Credits to Elevated https://github.com/ElevatedDev/Frequency
     */

    private double blockSlipperiness = 0.91;
    private double lastHorizontalDistance = 0.0;

    @Override
    public void onMove() {
        final Location from = new Location(data.getPlayer().getWorld(), data.getLastLocation().getX(), data.getLastLocation().getY(), data.getLastLocation().getZ());
        final Location to = new Location(data.getPlayer().getWorld(), data.getLocation().getX(), data.getLocation().getY(), data.getLocation().getZ());

        final Player player = data.getPlayer();

        final double deltaX = to.getX() - from.getX();
        final double deltaY = to.getY() - from.getY();
        final double deltaZ = to.getZ() - from.getZ();

        double blockSlipperiness = this.blockSlipperiness;
        double attributeSpeed = 1.0;

        final boolean onGround = data.isOnGround();

        attributeSpeed += PlayerUtils.getPotionEffectLevel(player, PotionEffectType.SPEED) * (float) 0.2 * attributeSpeed;
        attributeSpeed += PlayerUtils.getPotionEffectLevel(player, PotionEffectType.SPEED) * (float) -.15 * attributeSpeed;

        if (onGround) {
            blockSlipperiness *= 0.91f;

            if (data.isSprinting()) attributeSpeed *= 1.3;
            attributeSpeed *= 0.16277136 / Math.pow(blockSlipperiness, 3);

            if (deltaY > 0.4199 && data.isSprinting()) {
                attributeSpeed += 0.2;
            }
        } else {
            attributeSpeed = data.isSprinting() ? 0.026 : 0.02;

            blockSlipperiness = 0.91f;
        }

        if (data.isTakingVelocity()) attributeSpeed += Math.hypot(data.getLastVelocity().getX(), data.getLastVelocity().getZ());

        final double horizontalDistance = Math.hypot(deltaX, deltaZ);
        final double movementSpeed = (horizontalDistance - lastHorizontalDistance) / attributeSpeed;

        final boolean exempt = (data.getLastFlying() - data.getLastLastFlying()) < 30 || data.getPlayer().isFlying() || data.liquidTicks() < 10 || data.teleportTicks() < 10 || data.collidedVTicks() < 10 || data.getPlayer().isInsideVehicle();

        if (movementSpeed > 1.0 && !exempt) {
            buffer = Math.min(500, buffer + 10);

            if (buffer > 50) {
                flag();

                buffer /= 2;
            }
        }

        this.lastHorizontalDistance = horizontalDistance * blockSlipperiness;
        this.buffer = Math.max(buffer - 1, 0);

        this.blockSlipperiness = data.isOnGround() ? BlockUtils.getBlockFriction(data.getLocation()) * 0.91F : 0.91F;
    }
}
