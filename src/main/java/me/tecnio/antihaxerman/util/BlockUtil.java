

package me.tecnio.antihaxerman.util;

import me.tecnio.antihaxerman.AntiHaxerman;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.concurrent.FutureTask;

@UtilityClass
public final class BlockUtil {

    public double getBlockFriction(final Location to) {
        try {
            return (getBlockAsync(to).getType()) == Material.PACKED_ICE
                    || getBlockAsync(to).getType() == Material.ICE ? 0.9800000190734863
                    : (getBlockAsync(to).getType()).toString().toLowerCase().contains("slime") ? 0.800000011920929
                    : 0.6000000238418579;
        } catch (final Exception ignored) {
            return 0.6000000238418579;
        }
    }

    public boolean isLiquid(Block block) {
        return block.getType().toString().contains("WATER") || block.getType().toString().contains("LAVA");
    }

    public boolean isSlab(Block block) {
        return block.getType().toString().contains("STEP") || block.getType().toString().contains("SLAB");
    }

    public double getBlockFriction(final Block block) {
        return block.getType() == Material.PACKED_ICE || block.getType() == Material.ICE ? 0.9800000190734863 : block.getType().toString().contains("SLIME") ? 0.800000011920929 : 0.6000000238418579;
    }

    public Block getBlockAsync(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getWorld().getBlockAt(location);
        } else {
            return null;
        }
    }


    public Block shitAssBlockGetterThatCrashServer(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            final FutureTask<Block> futureTask = new FutureTask<>(() -> {
                location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                return location.getBlock();
            });
            Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), futureTask);
            try {
                return futureTask.get();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }
}
