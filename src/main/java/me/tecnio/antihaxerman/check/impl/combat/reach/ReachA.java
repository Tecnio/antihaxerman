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

package me.tecnio.antihaxerman.check.impl.combat.reach;

import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;
import me.tecnio.antihaxerman.util.PlayerUtil;
import me.tecnio.antihaxerman.util.type.BoundingBox;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

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
            if (data.getTargetLocations().size() < 20) return;

            final int now = data.getPositionProcessor().getTicks();
            final int latencyInTicks = MathUtil.msToTicks(PlayerUtil.getPing(data.getPlayer()));

            final double x = data.getPositionProcessor().getX();
            final double z = data.getPositionProcessor().getZ();

            final Vector origin = new Vector(x, 0.0, z);

            final double maxDistance = data.getPlayer().getGameMode() == GameMode.CREATIVE ? 6.1 : 3.1;
            final double distance = data.getTargetLocations().stream()
                    .filter(pair -> Math.abs(now - pair.getY() - latencyInTicks) < 4)
                    .mapToDouble(pair -> {
                        final Vector targetLocation = pair.getX().toVector().setY(0.0);
                        final BoundingBox boundingBox = new BoundingBox(targetLocation);

                        return Math.sqrt(Stream.of(
                                // BoundingBox corners.
                                origin.distanceSquared(new Vector(boundingBox.getMaxX(), 0.0, boundingBox.getMaxZ())),
                                origin.distanceSquared(new Vector(boundingBox.getMinX(), 0.0, boundingBox.getMinZ())),

                                // The ones left.
                                origin.distanceSquared(new Vector(boundingBox.getMinX(), 0.0, boundingBox.getMaxZ())),
                                origin.distanceSquared(new Vector(boundingBox.getMaxX(), 0.0, boundingBox.getMinZ())),

                                // Middles of the gay part.
                                origin.distanceSquared(new Vector(boundingBox.getMaxX() - 0.4D, 0.0, boundingBox.getMaxZ())),
                                origin.distanceSquared(new Vector(boundingBox.getMaxX(), 0.0, boundingBox.getMaxZ() - 0.4D)),

                                origin.distanceSquared(new Vector(boundingBox.getMinX() + 0.4D, 0.0, boundingBox.getMinZ())),
                                origin.distanceSquared(new Vector(boundingBox.getMinX(), 0.0, boundingBox.getMinZ() + 0.4D)),

                                // Distance from middle.
                                origin.distanceSquared(targetLocation)
                        ).mapToDouble(value -> value).min().orElse(-1));
                    })
                    .min().orElse(-1);

            final boolean invalid = distance > maxDistance;

            if (invalid) {
                if (increaseBuffer() > 3) {
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
