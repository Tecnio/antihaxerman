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

package me.tecnio.antihaxerman.check.impl.player.scaffold;

import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;
import me.tecnio.antihaxerman.util.BlockUtil;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

@CheckInfo(name = "Scaffold", type = "A", description = "Checks for invalid direction of placed block by checking for impossible eye position.")
public final class ScaffoldA extends Check {
    WrappedPacketInBlockPlace wrapper = null;
    public ScaffoldA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());
            //setting wrapper.
        } else if (packet.isFlying() && wrapper != null) {
            //Waiting for the next flying packet. We do this so we know for certain the player's position, with the exception of 0.03, but that shouldn't be an issue with this check.
            //Flying packet is C03PacketPlayer, C03PacketPlayer.C04PacketPlayerPosition, C03PacketPlayer.C05PacketPlayerLook or C03PacketPlayer.C06PacketPlayerPosLook. Most checks should wait for this packet, as it reveals the next look/position values for the player. If these are not updated it means the player is not changing their position or look values. A C04 is send every 20 ticks regardless of if moving or not.
            final Vector playerEyes = this.data.getPlayer().getEyeLocation().toVector();

            final double blockX = wrapper.getBlockPosition().getX();
            final double blockY = wrapper.getBlockPosition().getY();
            final double blockZ = wrapper.getBlockPosition().getZ();

            final Direction direction = wrapper.getDirection();
            
            Block block = BlockUtil.getBlockAsync(new Location(this.data.getPlayer().getWorld(), this.wrapper.getBlockPosition().getX(), this.wrapper.getBlockPosition().getY(), this.wrapper.getBlockPosition().getZ()));
            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.VEHICLE) || block.toString().toLowerCase().contains("door") || block.toString().toLowerCase().contains("ladder") || BlockUtil.getBlockAsync(this.data.getPlayer().getEyeLocation()).getType().isSolid();
            //Exempting possible desync in vehicles, placing against doors trapdoors or ladders or being inside a block.
            final boolean invalid = playerEyes.getY() > blockY && direction == Direction.DOWN || playerEyes.getY() < blockY && direction == Direction.UP || playerEyes.getX() > blockX && direction == Direction.WEST || blockX < playerEyes.getX() && direction == Direction.EAST || playerEyes.getZ() > blockZ && direction == Direction.NORTH || playerEyes.getZ() < blockZ && direction == Direction.SOUTH;
            //If you are above a block you can't, as a legit player, place on the bottom of that block. That concept is applied to all directions.
            if (invalid && !exempt) {
                fail("Face: " + direction);
            }
            wrapper = null;
            //setting wrapper as null so it doesn't check again later with a different position.
        }
    }
}
