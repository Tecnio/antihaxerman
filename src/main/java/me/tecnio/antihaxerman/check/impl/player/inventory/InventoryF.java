package me.tecnio.antihaxerman.check.impl.player.inventory;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Inventory", type = "F", description = "Checks if player is sprinting while sending close window packet.")
public class InventoryF extends Check {

    public InventoryF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isCloseWindow() && data.getActionProcessor().isSprinting()) {
            fail();
        }
    }
}
