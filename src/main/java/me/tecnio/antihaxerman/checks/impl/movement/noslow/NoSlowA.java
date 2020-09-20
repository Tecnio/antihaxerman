package me.tecnio.antihaxerman.checks.impl.movement.noslow;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

@CheckInfo(name = "NoSlow", type = "A")
public final class NoSlowA extends Check {
    public NoSlowA(PlayerData data) {
        super(data);
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event){
        if (event.getEntity() instanceof Player){
            final PlayerData data = DataManager.INSTANCE.getUser(event.getEntity().getUniqueId());
            if (!data.getPlayer().isInsideVehicle() && data.getSprintingTicks() > 5 && !data.getPlayer().isFlying() && data.isOnGround()){
                flag(data, "sprinting while shooting a bow!");
            }
        }
    }
}
