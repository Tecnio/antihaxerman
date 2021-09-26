package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.ServerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import io.github.retrooper.packetevents.utils.player.Direction;
import org.bukkit.Location;
import org.bukkit.block.Block;

@CheckInfo(name = "Scaffold", type = "G", description = "Checks if player is sprinting while bridging.")
public class ScaffoldG extends Check {

    public boolean placed;

    public ScaffoldG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isBlockPlace()) {
            WrappedPacketInBlockPlace wrapped = new WrappedPacketInBlockPlace(packet.getRawPacket());
            Block b = ServerUtil.getBlockAsync(new Location(data.getPlayer().getWorld(), wrapped.getBlockPosition().x, wrapped.getBlockPosition().y, wrapped.getBlockPosition().z));
            Direction dir = wrapped.getDirection();
            if(!placed) {
                return;
            }
            placed = false;
            if(dir == Direction.DOWN || dir == Direction.UP || dir == Direction.INVALID || dir == Direction.OTHER) {
                return;
            }
            if(isBridging() && data.getActionProcessor().isSprinting() && data.getRotationProcessor().getYaw() > 75 && increaseBuffer() > 30) {
                fail();
            }
            else {
                setBuffer(Math.max(0, getBuffer() -1));
            }
        }
        if(packet.isBukkitBlockPlace()) {
            placed = true;
        }
    }
}
