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

package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

@CheckInfo(name = "Aim", type = "D", description = "Checks for unlikely rotation relations.")
public final class AimD extends Check {

    public AimD(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isRotation()) {
            final boolean cinematic = data.getRotationProcessor().isCinematic();

            final float deltaPitch = data.getRotationProcessor().getDeltaPitch();
            final float deltaYaw = data.getRotationProcessor().getDeltaYaw();

            final boolean invalid = deltaPitch == 0 && deltaYaw >= 3.0F ||  deltaYaw == 0 && deltaPitch >= 3.0F;
            //Unlikely to rotate only on one axis for multiple ticks.
            
            if (this.data.getRotationProcessor().getSensitivity() > 140 && (this.data.getRotationProcessor().getMouseDeltaY() < 2 || this.data.getRotationProcessor().getMouseDeltaX() < 2)) {
                return;
                //High sensitivity might cause this however.
            }
            if (invalid && !cinematic && Math.abs(data.getRotationProcessor().getPitch()) != 90) {
                //When abs(pitch) = 90 weird rotation relations come about.
                if (increaseBuffer() > 4) {
                    fail("DeltaYaw: " + deltaYaw + " deltaPitch: " + deltaPitch);
                    setBuffer(3.5);
                }
            } else {
                decreaseBufferBy(2.5);
            }
        }
    }
}
