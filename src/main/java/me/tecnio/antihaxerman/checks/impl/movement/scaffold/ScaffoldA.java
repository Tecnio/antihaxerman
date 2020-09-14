package me.tecnio.antihaxerman.checks.impl.movement.scaffold;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.checks.SetBackType;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

@CheckInfo(name = "Scaffold", type = "A")
public final class ScaffoldA extends Check {
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        final PlayerData data = DataManager.INSTANCE.getUser(event.getPlayer().getUniqueId());

        if (event.getClickedBlock() != null && data != null && !data.getPlayer().isInsideVehicle()){
            if (data.isOnGround()
                    && data.getLocation().getBlock().getLocation().clone().subtract(0,1,0).getBlock().equals(event.getClickedBlock())
                    && data.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()){
                if (event.getBlockFace().equals(BlockFace.DOWN)){
                    flag(data, "invalid block placement.", SetBackType.BACK);
                }
            }
        }
    }
}