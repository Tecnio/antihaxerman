package me.tecnio.antihaxerman.checks.impl.combat.criticals;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.block.BlockFace;

@CheckInfo(name = "Criticals", type = "A")
public class CriticalsA extends Check {

    private boolean attacked;

    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if (e.getPacketId() == PacketType.Client.USE_ENTITY){
            attacked = true;
        }else if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())){
            if (attacked){
                if (data.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()
                        || data.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid())
                    return;

                if (!data.isOnGround() && !data.getPlayer().getAllowFlight()) {
                    if (data.getLocation().getY() % 1.0D == 0.0D) {
                        if (data.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                            if (preVL++ >= 2) {
                                flag(data, "tried to hit critical on ground!");
                            }
                        }
                    } else preVL = 0;
                }
                attacked = false;
            }
        }
    }
}
