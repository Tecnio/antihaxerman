

package me.tecnio.antihaxerman.check.impl.movement.jesus;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.block.Block;

import java.util.List;

@CheckInfo(name = "Jesus", type = "A", description = "Checks if player is walking on liquids.")
public final class JesusA extends Check {
    public JesusA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final List<Block> blocks = data.getPositionProcessor().getBlocks();
            final List<Block> blocksBelow = data.getPositionProcessor().getBlocksBelow();

            if (blocks == null || blocksBelow == null) return;

            final boolean onLiquid = blocksBelow.stream().allMatch(Block::isLiquid);
            final boolean noBlock = blocksBelow.stream().anyMatch(block -> block.getType().isSolid());

            final boolean clientGround = data.getPositionProcessor().isOnGround();
            final boolean serverGround = data.getPositionProcessor().isMathematicallyOnGround();

            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.CHUNK);
            final boolean invalid = (clientGround || serverGround) && onLiquid && !noBlock;

            if (invalid && !exempt) {
                if (increaseBuffer() > 5) {
                    fail();
                }
            } else {
                decreaseBufferBy(0.50);
            }
        }
    }
}
