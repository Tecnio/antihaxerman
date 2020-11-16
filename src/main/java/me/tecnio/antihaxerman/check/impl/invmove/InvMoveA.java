package me.tecnio.antihaxerman.check.impl.invmove;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.player.PlayerUtils;
import org.bukkit.GameMode;

@CheckInfo(name = "InvMove", type = "A")
public final class InvMoveA extends Check {
    public InvMoveA(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.WINDOW_CLICK) {
            final boolean exempt = data.isInWeb() || data.flyingTicks() < 20 || data.pistonTicks() < 10 || data.liquidTicks() < 20 || data.climbableTicks() < 20 || data.isTakingVelocity() || data.getPlayer().getGameMode().equals(GameMode.CREATIVE);
            final boolean invalid = data.getDeltaXZ() > PlayerUtils.getBaseSpeed(data.getPlayer(), 0.15F) && data.isOnGround();

            if (invalid && !exempt) {
                if (increaseBuffer() > 3) {
                    flag();
                }
            } else {
                decreaseBuffer();
            }
        }
    }
}
