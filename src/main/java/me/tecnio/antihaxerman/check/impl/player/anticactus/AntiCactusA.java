package me.tecnio.antihaxerman.check.impl.player.anticactus;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.Material;

@CheckInfo(name = "AntiCactus", type = "A", description = "So ahm would be 0/10 if there would be no anti cactus check so here it is")
public class AntiCactusA extends Check {

    public AntiCactusA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying() && !isExempt(ExemptType.CREATIVE, ExemptType.JOINED, ExemptType.HURT)) {
            if (data.getPlayer().getLocation().add(0, 0, -0.31).getBlock().getType() == Material.CACTUS) {
                fail();
            }
            if (data.getPlayer().getLocation().add(0, 0, 0.31).getBlock().getType() == Material.CACTUS) {
                fail();
            }
            if (data.getPlayer().getLocation().add(0.31, 0, 0).getBlock().getType() == Material.CACTUS) {
                fail();
            }
            if (data.getPlayer().getLocation().add(-0.31, 0, 0).getBlock().getType() == Material.CACTUS) {
                fail();
            }
        }
    }
}
