package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.type.BlockUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import org.bukkit.Location;
import org.bukkit.block.Block;

@CheckInfo(name = "Scaffold", type = "J", description = "Checks if player is interacting with a liquid.")
public class ScaffoldJ extends Check {

    public ScaffoldJ(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isBlockDig()) {
            final WrappedPacketInBlockDig wrapper = new WrappedPacketInBlockDig(packet.getRawPacket());

            final Block block = BlockUtil.getBlockAsync(new Location(data.getPlayer().getWorld(), wrapper.getBlockPosition().x, wrapper.getBlockPosition().y, wrapper.getBlockPosition().z));
            if (block == null) return;

            if (block.isLiquid()) {
                fail();
            }
        }
    }
}
