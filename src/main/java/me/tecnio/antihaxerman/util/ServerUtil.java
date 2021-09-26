

package me.tecnio.antihaxerman.util;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.block.Block;

@UtilityClass
public class ServerUtil {

    public double getTPS() {
        return Math.min(20.0, PacketEvents.get().getServerUtils().getTPS());
    }

    public ServerVersion getServerVersion() {
        return PacketEvents.get().getServerUtils().getVersion();
    }

    public boolean isLowerThan1_8() {
        return getServerVersion().isLowerThan(ServerVersion.v_1_8);
    }

    public static Block getBlockAsync(Location loc) {
        if (loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4))
            return loc.getBlock();
        return null;
    }

}
