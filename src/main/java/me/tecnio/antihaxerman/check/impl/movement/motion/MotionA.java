

package me.tecnio.antihaxerman.check.impl.movement.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "A", description = "Checks for invalid jump motion.")
public final class MotionA extends Check {
    public MotionA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isFlying()) {
            final boolean onGround = data.getPositionProcessor().isOnGround();

            final double deltaY = data.getPositionProcessor().getDeltaY();

            final double y = data.getPositionProcessor().getY();
            final double lastY = data.getPositionProcessor().getLastY();

            final boolean step = y % 0.015625 == 0.0 && lastY % 0.015625 == 0.0;

            final double modifierJump = PlayerUtil.getPotionLevel(data.getPlayer(), PotionEffectType.JUMP) * 0.1F;
            final double expectedJumpMotion = 0.42F + modifierJump;

            final boolean exempt = isExempt(ExemptType.BOAT, ExemptType.LAGGINGHARD, ExemptType.LAGGING, ExemptType.NEARSLIME, ExemptType.RESPAWN, ExemptType.VEHICLE, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.PISTON,
                    ExemptType.LIQUID, ExemptType.TELEPORT, ExemptType.WEB, ExemptType.BOAT, ExemptType.FLYING, ExemptType.SLIME,
                    ExemptType.UNDERBLOCK, ExemptType.CHUNK) || data.getPositionProcessor().getSinceBlockNearHeadTicks() < 5;
            final boolean invalid = deltaY != expectedJumpMotion && deltaY > 0.0 && !onGround && lastY % 0.015625 == 0.0 && !step && deltaY != 0.36075000000000035;
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_12)) {
                if(this.isExempt(ExemptType.ONBED)) {
                    return;
                }
            }
            for(PotionEffect pot : data.getPlayer().getActivePotionEffects()) {
                if(pot.getAmplifier() > 200){
                    return;
                }
            }
            if (invalid && !exempt) fail("DeltaY: " + deltaY + " Velocity: " + isExempt(ExemptType.VELOCITY));
            if (step && deltaY > 0.6F && !exempt) fail();
        }
    }
}
