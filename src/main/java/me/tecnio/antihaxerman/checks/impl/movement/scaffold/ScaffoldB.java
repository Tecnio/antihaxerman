package me.tecnio.antihaxerman.checks.impl.movement.scaffold;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.checks.SetBackType;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

@CheckInfo(name = "Scaffold", type = "B")
public class ScaffoldB extends Check {
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        PlayerData data = DataManager.INSTANCE.getUser(event.getPlayer().getUniqueId());
        Vector blockVec = event.getBlockPlaced().getLocation().toVector();
        double dist = data.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation().toVector().distance(blockVec);
        double diff = data.getDeltaYaw();
        if(diff > 100 && dist <= 2.0 && event.getBlockPlaced().getType().isSolid()) {
            if(++preVL > 4) {
                flag(data, "suspicious rotations, r: " + diff + ", d: " + dist, SetBackType.BACK);
            }
        }else preVL = 0;
    }
}
