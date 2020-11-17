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

package me.tecnio.antihaxerman.utils.player;

import lombok.experimental.UtilityClass;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.math.MathUtils;
import me.tecnio.antihaxerman.utils.other.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

@UtilityClass
public final class CollisionUtils {

    public boolean isOnGround(PlayerData data) {
        return data.getCollidedBlocks().stream().anyMatch(block -> !block.isEmpty() && !block.isLiquid());
    }

    public boolean isOnAir(PlayerData data) {
        return data.getCollidedBlocks().stream().allMatch(block -> block.getType() == Material.AIR);
    }

    public boolean isInLiquid(PlayerData data) {
        return data.getCollidedBlocks().stream().anyMatch(Block::isLiquid);
    }

    public boolean isOnClimbable(PlayerData data) {
        return data.getCollidedBlocks().stream().anyMatch(block -> block.getType() == Material.LADDER || block.getType() == Material.VINE);
    }

    public boolean isInWeb(PlayerData data) {
        return data.getCollidedBlocks().stream().anyMatch(block -> block.getType() == Material.WEB);
    }

    public static boolean blockNearHead(PlayerData data) {
        final Player player = data.getPlayer();

        double expand = 0.4;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (player.getLocation().clone().add(z, 2.01, x).getBlock().getType() != Material.AIR) {
                    return true;
                }
                if (player.getLocation().clone().add(z, 1.5001, x).getBlock().getType() != Material.AIR) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean nearWall(PlayerData data) {
        final Player player = data.getPlayer();
        final double expand = 0.6;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (player.getLocation().clone().add(z, 0.1, x).getBlock().getType() != Material.AIR) {
                    return true;
                }
                if (player.getEyeLocation().clone().add(z, 0, x).getBlock().getType() != Material.AIR) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onBoat(PlayerData data) {
        List<Entity> entityList = data.getPlayer().getNearbyEntities(1.5, 1.5, 1.5);

        for (Entity entity : entityList) {
            if (entity instanceof Boat) {
                return true;
            }
        }

        return false;
    }

    /*
    * Just reverse server code and edit a bit bc I cannot bother doing it myself.
     */
    public List<Block> handleCollisions(PlayerData data) {
        List<Block> blockList = new ArrayList<>();

        BoundingBox boundingBox = new BoundingBox(data.getPlayer());
        boundingBox = boundingBox.add(0, -0.1F, 0, 0, 0, 0);

        int i = MathUtils.floor(boundingBox.minX);
        int j = MathUtils.floor(boundingBox.maxX + 1.0D);
        int k = MathUtils.floor(boundingBox.minY);
        int l = MathUtils.floor(boundingBox.maxY + 1.0D);
        int i1 = MathUtils.floor(boundingBox.minZ);
        int j1 = MathUtils.floor(boundingBox.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    final Block block = getBlockAsync(new Location(data.getPlayer().getWorld(), k1, l1, i2));
                    blockList.add(block);
                }
            }
        }

        return blockList;
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
