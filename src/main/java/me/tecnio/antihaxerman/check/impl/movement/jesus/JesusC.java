package me.tecnio.antihaxerman.check.impl.movement.jesus;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.BlockUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Material;

@CheckInfo(name = "Jesus", type = "C", description = "Checks if player is walking on liquids.")
public class JesusC extends Check {

    public JesusC(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying()) {
            if (BlockUtil.isLiquid(data.getPlayer().getLocation().subtract(0, 0.1, 0).getBlock())
                    && !BlockUtil.isLiquid(data.getPlayer().getLocation().clone().add(0, 0.2, 0).getBlock())
                    && !data.getVelocityProcessor().isTakingVelocity()
                    && data.getPositionProcessor().getBlocksBelow().stream().noneMatch(block -> block.getType() == Material.WATER_LILY)
                    && data.getPositionProcessor().getWebTicks() == 0) {

                if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                    return;
                }

                if (!data.getPositionProcessor().isOnGround() && increaseBuffer() > 9 && !isExempt(ExemptType.FLYING)) {
                    fail("DeltaY:" + data.getPositionProcessor().getDeltaY());
                }
                else {
                    decreaseBufferBy(0.5);
                }
            }
        }
    }
}
