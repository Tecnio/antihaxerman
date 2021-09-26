

package me.tecnio.antihaxerman.check.impl.movement.liquidspeed;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@CheckInfo(name = "LiquidSpeed", type = "C", description = "Checks for horizontal speed under water.")
public final class LiquidSpeedC extends Check {
    public LiquidSpeedC(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean isFullySubmerged = data.getPositionProcessor().isFullySubmergedInLiquidStat();
            final boolean onGround = data.getPositionProcessor().isOnGround();

            final boolean sprinting = data.getActionProcessor().isSprinting();

            final double deltaX = data.getPositionProcessor().getDeltaX();
            final double deltaZ = data.getPositionProcessor().getDeltaZ();

            final double lastDeltaX = data.getPositionProcessor().getLastDeltaX();
            final double lastDeltaZ = data.getPositionProcessor().getLastDeltaZ();

            final ItemStack boots = data.getPlayer().getInventory().getBoots();

            float f1 = 0.8F;
            float f3;

            if (boots != null) f3 = boots.getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
            else f3 = 0.0F;

            if (f3 > 3.0F) f3 = 3.0F;
            if (!onGround) f3 *= 0.5F;
            if (f3 > 0.0F) f1 += (0.54600006F - f1) * f3 / 3.0F;

            final double predictedX = lastDeltaX * f1 + (sprinting ? 0.0263 : 0.02);
            final double predictedZ = lastDeltaZ * f1 + (sprinting ? 0.0263 : 0.02);

            final double differenceX = deltaX - predictedX;
            final double differenceZ = deltaZ - predictedZ;

            final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.VEHICLE, ExemptType.FLYING,
                    ExemptType.PISTON, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.WEB,
                    ExemptType.SLIME, ExemptType.BOAT, ExemptType.CHUNK);
            final boolean invalid = (differenceX > 0.05 || differenceZ > 0.05) && isFullySubmerged;

            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                return;
            }

            if (invalid && !exempt) {
                if (increaseBuffer() > 2) {
                    fail(String.format("diffX: %.3f diffZ: %.3f", differenceX, differenceZ));
                }
            } else {
                decreaseBufferBy(0.25);
            }
        }
    }
}
