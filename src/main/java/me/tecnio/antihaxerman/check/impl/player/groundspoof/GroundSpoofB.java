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

package me.tecnio.antihaxerman.check.impl.player.groundspoof;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "GroundSpoof", type = "B", description = "Compares server fall distance to client fall distance.")
public final class GroundSpoofB extends Check {

    private double serverFallDistance;

    public GroundSpoofB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosition()) {
            final double deltaY = data.getPositionProcessor().getDeltaY();

            final boolean inAir = data.getPositionProcessor().isInAir();
            final boolean nearStair = data.getPositionProcessor().isNearStair();
            final boolean inLiquid = data.getPositionProcessor().isInLiquid();

            if (deltaY < 0.0 && !inAir && !nearStair && !inLiquid) {
                serverFallDistance -= deltaY;
            } else {
                serverFallDistance = 0.0;
            }

            final double serverFallDistance = this.serverFallDistance;
            final double clientFallDistance = data.getPlayer().getFallDistance();

            final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.CREATIVE, ExemptType.WEB, ExemptType.CLIMBABLE,ExemptType.LIQUID, ExemptType.BOAT, ExemptType.VOID, ExemptType.VEHICLE, ExemptType.CHUNK, ExemptType.PISTON);
            final boolean invalid = Math.abs(serverFallDistance - clientFallDistance) - clientFallDistance >= 1.0 && inAir;

            if (invalid && !exempt) {
                if (increaseBuffer() > 4) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
