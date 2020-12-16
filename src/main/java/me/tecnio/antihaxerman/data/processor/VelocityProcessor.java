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

package me.tecnio.antihaxerman.data.processor;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import lombok.Getter;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.data.PlayerData;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class VelocityProcessor {

    private final PlayerData data;
    private double velocityX, velocityY, velocityZ;
    private double lastVelocityX, lastVelocityY, lastVelocityZ;
    private int maxVelocityTicks, velocityTicks, ticksSinceVelocity;
    private short transactionID, velocityID;
    private long transactionPing, transactionReply;
    private boolean verifyingVelocity;

    public VelocityProcessor(final PlayerData data) {
        this.data = data;
    }

    public void handle(final double velocityX, final double velocityY, final double velocityZ) {
        this.ticksSinceVelocity = 0;

        lastVelocityX = this.velocityX;
        lastVelocityY = this.velocityY;
        lastVelocityZ = this.velocityZ;

        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;

        this.velocityID = (short) ThreadLocalRandom.current().nextInt(32767);
        this.verifyingVelocity = true;
        PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), new WrappedPacketOutTransaction(0, velocityID, false));
    }

    public void handleTransaction(final WrappedPacketInTransaction wrapper) {
        if (this.verifyingVelocity && wrapper.getActionNumber() == this.velocityID) {
            this.verifyingVelocity = false;
            this.velocityTicks = AntiHaxerman.INSTANCE.getTickManager().getTicks();
            this.maxVelocityTicks = (int) (((lastVelocityZ + lastVelocityX) / 2 + 2) * 15);
        }

        if (wrapper.getActionNumber() == transactionID) {
            transactionPing = System.currentTimeMillis() - transactionReply;

            transactionID = (short) ThreadLocalRandom.current().nextInt(32767);
            PacketEvents.get().getPlayerUtils().sendPacket(data.getPlayer(), new WrappedPacketOutTransaction(0, transactionID, false));
            transactionReply = System.currentTimeMillis();
        }
    }

    public void handleFlying() {
        ++ticksSinceVelocity;
    }

    public boolean isTakingVelocity() {
        return Math.abs(AntiHaxerman.INSTANCE.getTickManager().getTicks() - this.velocityTicks) < this.maxVelocityTicks;
    }
}
