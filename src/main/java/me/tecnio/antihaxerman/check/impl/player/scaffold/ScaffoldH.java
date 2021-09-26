package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.BlockUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

@CheckInfo(name = "Scaffold", type = "H", description = "Checks for expanded scaffold.")
public class ScaffoldH extends Check {

    public ScaffoldH(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockPlace()) {
            final WrappedPacketInBlockPlace wrapper = new WrappedPacketInBlockPlace(packet.getRawPacket());

            final Location blockLocation = new Location(data.getPlayer().getWorld(),
                    wrapper.getBlockPosition().getX(),
                    wrapper.getBlockPosition().getY(),
                    wrapper.getBlockPosition().getZ()
            );
            final Direction direction = wrapper.getDirection();

            final Block block = BlockUtil.getBlockAsync(blockLocation);
            if (block == null) return;

            if (block.getType().isSolid() && block.getType() == Material.TRAP_DOOR) {
                final double x = data.getPositionProcessor().getX();
                final double y = data.getPositionProcessor().getY();
                final double z = data.getPositionProcessor().getZ();

                if ((y - block.getX()) > 0.45) {
                    final Location location = new Location(data.getPlayer().getWorld(), x, y + data.getPlayer().getEyeHeight(), z);

                    final boolean invalid = !interactedCorrectly(blockLocation, location, direction);

                    if (invalid) {
                        if (increaseBuffer() > 1) {
                            fail();
                        }
                    } else {
                        resetBuffer();
                    }
                }
            }
        }
    }

    private boolean interactedCorrectly(final Location blockLoc, final Location playerLoc, final Direction face) {
        switch (face) {
            case UP: {
                return true;
            }
            case DOWN: {
                final double limit = blockLoc.getY() - 0.03;
                return playerLoc.getY() < limit;
            }
            case WEST: {
                final double limit = blockLoc.getX() + 0.03;
                return limit > playerLoc.getX();
            }
            case EAST: {
                final double limit = blockLoc.getX() - 0.03;
                return playerLoc.getX() > limit;
            }
            case NORTH: {
                final double limit = blockLoc.getZ() + 0.03;
                return playerLoc.getZ() < limit;
            }
            case SOUTH: {
                final double limit = blockLoc.getZ() - 0.03;
                return playerLoc.getZ() > limit;
            }

            default: return true;
        }
    }
}
