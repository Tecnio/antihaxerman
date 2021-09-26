package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.Location;
import org.bukkit.event.block.BlockPlaceEvent;

@CheckInfo(name = "Scaffold", type = "I", description = "Checks for tower scaffold.")
public class ScaffoldI extends Check {

    private Location lastLocation;
    private int ticksSincePlace;
    private int lastX, lastZ;

    public ScaffoldI(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBukkitBlockPlace()) {
            final BlockPlaceEvent event = (BlockPlaceEvent) packet.getRawPacket().getRawNMSPacket();

            final Location location = event.getBlock().getLocation();

            final double deltaY = data.getPositionProcessor().getDeltaY();
            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final int x = (int) data.getPositionProcessor().getX();
            final int z = (int) data.getPositionProcessor().getZ();
            if(lastX == x || lastZ == z) {
                return;
            }
            lastX = x;
            lastZ = z;
            debug(deltaY);
            final boolean invalid = ticksSincePlace < 7 && placementUnder(location) && deltaY > 0.0 && deltaXZ < 1;

            if (invalid) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.50);
            }

            lastLocation = location;
            ticksSincePlace = 0;
        } else if (packet.isFlying()) {
            ++ticksSincePlace;
        }
    }

    private boolean placementUnder(final Location blockLocation) {
        final double x = data.getPositionProcessor().getX();
        final double y = data.getPositionProcessor().getY();
        final double z = data.getPositionProcessor().getZ();

        final double blockX = blockLocation.getX();
        final double blockY = blockLocation.getY();
        final double blockZ = blockLocation.getZ();

        double lastBlockY;
        try {
            lastBlockY = lastLocation.getY();
        } catch (NullPointerException exception) {
            lastBlockY = 696969;
        }
        if(lastBlockY == 696969) {
            return false;
        }
        return Math.floor(y - 0.25) == blockY
                && blockY < y
                && lastBlockY < y
                && lastBlockY < blockY
                && Math.abs(x - blockX) <= 0.8
                && Math.abs(z - blockZ) <= 0.8;
    }
}
