package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.ServerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

@CheckInfo(name = "Scaffold", type = "F", description = "Checks for invalid placement.")
public class ScaffoldF extends Check {

    public ScaffoldF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isBlockPlace()) {
            WrappedPacketInBlockPlace place = new WrappedPacketInBlockPlace(packet.getRawPacket());
            Location locnew = new Location(data.getPlayer().getWorld(), place.getBlockPosition().x, place.getBlockPosition().y, place.getBlockPosition().z);
            Block targetedBlock = ServerUtil.getBlockAsync(locnew);
            if(targetedBlock == null)
                return;
            Material mat = targetedBlock.getType();
            if(targetedBlock.isLiquid() || mat == Material.AIR) {
            }
        }
    }
}
