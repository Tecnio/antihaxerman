

package me.tecnio.antihaxerman.check.impl.combat.autoblock;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.Material;

@CheckInfo(name = "AutoBlock", type = "B", description = "Checks if player is blocking in a unlikely manner.")
public class AutoBlockB extends Check {

    private boolean attacked;
    private int ticks;

    public AutoBlockB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isUseEntity()) {
            attacked = true;
        } else if (packet.isBlockPlace()) {
            final double cps = data.getClickProcessor().getCps();
            if(data.getPlayer().getInventory().getItemInHand().getType() == Material.FISHING_ROD || data.getPlayer().getItemInHand().getType().toString().contains("SWORD")) {
                return;
            }
            if (attacked) {
                if (ticks < 2 && cps > 6.0) {
                    if (increaseBuffer() > 4) {
                        fail();
                    }
                } else {
                    resetBuffer();
                }
                attacked = false;
            }

            ticks = 0;
        } else if (packet.isFlying()) {
            ++ticks;
        }
    }
}
