

package me.tecnio.antihaxerman.check.impl.player.inventory;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;

@CheckInfo(name = "Inventory", type = "B", description = "Checks if player is moving while interacting with inventory.")
public final class InventoryB extends Check {
    public InventoryB(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isWindowClick()) {
            final boolean onGround = data.getPositionProcessor().isOnGround();

            final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

            final double acceleration = deltaXZ - lastDeltaXZ;

            final boolean exempt = isExempt(ExemptType.WEB, ExemptType.FLYING, ExemptType.PISTON, ExemptType.LIQUID, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.CREATIVE);

            final boolean invalidDelta = deltaXZ > PlayerUtil.getBaseSpeed(data.getPlayer(), 0.2F) && onGround;
            final boolean invalidAcceleration = acceleration >= 0.0 && deltaXZ > PlayerUtil.getBaseSpeed(data.getPlayer(), 0.1F);

            final boolean invalid = invalidDelta || invalidAcceleration;

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.5);
            }
        }
    }
}
