package me.tecnio.antihaxerman.checks.impl.player.invmove;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

@CheckInfo(name = "InvMove", type = "A")
public final class InvMoveA extends Check {
    public InvMoveA(PlayerData data) {
        super(data);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        final PlayerData data = DataManager.INSTANCE.getUser(event.getWhoClicked().getUniqueId());

        if (data.isInWeb() || data.isInLiquid() || data.isOnClimbableBlock() || data.isTakingVelocity() || event.getClick() == ClickType.CREATIVE || event.getAction() == InventoryAction.PLACE_ALL) return;

        if (data.isOnGround() && data.getDeltaXZ() > 0.15) {
            if (++preVL > 2) {
                flag(data, "player used inventory while moving.");
            }
        } else preVL = 0;
    }
}
