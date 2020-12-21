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

package me.tecnio.antihaxerman.util;

import lombok.experimental.UtilityClass;
import me.tecnio.antihaxerman.AntiHaxerman;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.concurrent.FutureTask;

@UtilityClass
public final class BlockUtil {

    public double getBlockFriction(final Location to) {
        try {
            return (getBlockAsync(to.clone().subtract(0.0, 0.3, 0.0)).getType()) == Material.PACKED_ICE
                    || getBlockAsync(to.clone().subtract(0.0, 1.0, 0.0)).getType() == Material.ICE ? 0.9800000190734863
                    : (getBlockAsync(to.clone().subtract(0.0, 1.0, 0.0)).getType()).toString().toLowerCase().contains("slime") ? 0.800000011920929
                    : 0.6000000238418579;
        } catch (Exception ignored) {
            return 0.6000000238418579;
        }
    }

    public double getBlockFriction(final Block block) {
        return block.getType() == Material.PACKED_ICE || block.getType() == Material.ICE ? 0.9800000190734863 : block.getType().toString().contains("SLIME") ? 0.800000011920929 : 0.6000000238418579;
    }

    //Taken from Fiona. If you have anything better, please let me know, thanks.
    public Block getBlockAsync(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            FutureTask<Block> futureTask = new FutureTask<>(() -> {
                location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                return location.getBlock();
            });
            Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), futureTask);
            try {
                return futureTask.get();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }
}
