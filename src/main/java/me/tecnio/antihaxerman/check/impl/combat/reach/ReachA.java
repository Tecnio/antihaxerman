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

package me.tecnio.antihaxerman.check.impl.combat.reach;

import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach", type = "A", description = "Checks if player is attacking from a distance that's not possible.")
public final class ReachA extends Check {

    private boolean attacked;

    public ReachA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            if (!attacked) return;
            attacked = false;

            final Entity target = data.getCombatProcessor().getTarget();
            final Entity lastTarget = data.getCombatProcessor().getLastTarget();

            if (target != lastTarget) return;

            if (!(target instanceof Player)) return;
            if (data.getTargetLocations().size() < 30) return;

            final int now = AntiHaxerman.INSTANCE.getTickManager().getTicks();
            final int latencyInTicks = (int) Math.floor(data.getConnectionProcessor().getTransactionPing() / 50.0);

            final boolean accepted = data.getConnectionProcessor().getKeepAliveTime(now).isPresent();

            final double x = data.getPositionProcessor().getX();
            final double z = data.getPositionProcessor().getZ();

            final Vector origin = new Vector(x, 0.0, z);

            final double maxDistance = data.getPlayer().getGameMode() == GameMode.CREATIVE ? 6.0 : 3.0;
            final double distance = data.getTargetLocations().stream()
                    .filter(pair -> Math.abs(now - pair.getY() - latencyInTicks) < 3)
                    .mapToDouble(pair -> {
                        final Vector targetLocation = pair.getX().toVector().setY(0.0);

                        return origin.distance(targetLocation) - 0.5658;
                    })
                    .min().orElse(-1);

            final boolean invalid = distance > maxDistance && accepted;

            if (invalid) {
                if (increaseBuffer() > 2) {
                    fail(distance);
                }
            } else {
                decreaseBufferBy(0.05);
            }
        } else if (packet.isUseEntity()) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                attacked = true;
            }
        }
    }
}
