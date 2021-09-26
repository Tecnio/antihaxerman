package me.tecnio.antihaxerman.check.impl.player.inventory;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Inventory", type = "E", description = "Checks if player is sending entity action packet while in inventory.")
public class InventoryE extends Check {

    public InventoryE(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isEntityAction()) {
            if(data.getActionProcessor().isInventory()) {
                if(increaseBuffer() > 1) {
                    fail();
                }
                else {
                    setBuffer(0);
                }
            }
        }
    }
}
