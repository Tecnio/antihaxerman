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

package me.tecnio.antihaxerman.data.processor;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.type.Velocity;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class VelocityProcessor {

    private final PlayerData data;

    private double velocityX, velocityY, velocityZ, velocityXZ;
    private double lastVelocityX, lastVelocityY, lastVelocityZ, lastVelocityXZ;

    private int maxVelocityTicks, velocityTicks, ticksSinceVelocity, takingVelocityTicks;
    private short velocityID;

    private final Map<Short, Vector> pendingVelocities = new HashMap<>();
    private final Velocity transactionVelocity = new Velocity(0, 0, 0, 0);

    private int flyingTicks;

    public VelocityProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final double velocityX, final double velocityY, final double velocityZ) {
        lastVelocityX = this.velocityX;
        lastVelocityY = this.velocityY;
        lastVelocityZ = this.velocityZ;
        lastVelocityXZ = this.velocityXZ;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.velocityXZ = Math.hypot(velocityX, velocityZ);

        this.velocityID = (short) ThreadLocalRandom.current().nextInt(Short.MAX_VALUE);

        PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(),
                new WrappedPacketOutTransaction(0, velocityID, false));
        pendingVelocities.put(velocityID, new Vector(velocityX, velocityY, velocityZ));
    }

    public void handleTransaction(final WrappedPacketInTransaction wrapper) {
        pendingVelocities.computeIfPresent(wrapper.getActionNumber(), (id, vector) -> {
            this.ticksSinceVelocity = 0;

            transactionVelocity.setVelocityX(vector.getX());
            transactionVelocity.setVelocityY(vector.getY());
            transactionVelocity.setVelocityZ(vector.getZ());

            transactionVelocity.setIndex(transactionVelocity.getIndex() + 1);

            this.velocityTicks = flyingTicks;
            this.maxVelocityTicks = (int) (((vector.getX() + vector.getZ()) / 2 + 2) * 15);

            pendingVelocities.remove(wrapper.getActionNumber());

            return vector;
        });
    }

    public void handleFlying() {
        ++ticksSinceVelocity;
        ++flyingTicks;

        if (isTakingVelocity()) {
            ++takingVelocityTicks;
        } else {
            takingVelocityTicks = 0;
        }
    }

    public boolean isTakingVelocity() {
        return Math.abs(flyingTicks - this.velocityTicks) < this.maxVelocityTicks;
    }
}
