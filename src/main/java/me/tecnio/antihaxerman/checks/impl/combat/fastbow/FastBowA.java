package me.tecnio.antihaxerman.checks.impl.combat.fastbow;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;

@CheckInfo(name = "FastBow", type = "A")
public class FastBowA extends Check {
    @EventHandler
    public void onBowShoot(EntityShootBowEvent event){
        if (event.getEntity() instanceof Player){
            PlayerData data = DataManager.INSTANCE.getUser(event.getEntity().getUniqueId());

            long shootTime = time();
            long diff = elapsed(time(), data.getLastShoot());

            if (event.getProjectile().getVelocity().length() > .14 && diff < 190) {
                flag(data, "throwing arrows faster than normal.");
            }

            data.setLastShoot(shootTime);
        }
    }
}
