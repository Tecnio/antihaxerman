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

package me.tecnio.antihaxerman.utils.world;

import me.tecnio.antihaxerman.AntiHaxerman;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.FutureTask;

@UtilityClass
public final class BlockUtils {
    public double getBlockFriction(Location to) {
        try {
            return (getBlockAsync(to.clone().subtract(0.0, 0.3, 0.0)).getType()) == Material.PACKED_ICE
                    || getBlockAsync(to.clone().subtract(0.0, 1.0, 0.0)).getType() == Material.ICE ? 0.9800000190734863
                    : (getBlockAsync(to.clone().subtract(0.0, 1.0, 0.0)).getType()).toString().toLowerCase().contains("slime") ? 0.800000011920929
                    : 0.6000000238418579;
        } catch (Exception ignored) {
            return 0.6000000238418579;
        }
    }

    public static List<Material> getNearbyBlocks(Location location, int radius) {
        final List<Material> nearby = new ArrayList<>();
        final int blockX = location.getBlockX();
        final int blockY = location.getBlockY();
        final int blockZ = location.getBlockZ();
        for (int x = blockX - radius; x <= blockX + radius; x++) {
            for (int y = blockY - radius; y <= blockY + radius; y++) {
                for (int z = blockZ - radius; z <= blockZ + radius; z++) {
                    nearby.add(Objects.requireNonNull(getBlockAsync(new Location(location.getWorld(), x, y, z))).getType());
                }
            }
        }
        return nearby;
    }

    public Block getBlockAsync(Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            FutureTask<Block> futureTask = new FutureTask<>(() -> {
                location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                return location.getBlock();
            });
            Bukkit.getScheduler().runTask(AntiHaxerman.getInstance(), futureTask);
            try {
                return futureTask.get();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }
}
