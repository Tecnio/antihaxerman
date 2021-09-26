package me.tecnio.antihaxerman.check.impl.movement.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion", type = "F", description = "Checks for jump height/vertical movement threshold.")
public class MotionF extends Check {

    public MotionF(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isPosition()) {
            if(PacketEvents.get().getPlayerUtils().getClientVersion(data.getPlayer()).isNewerThanOrEquals(ClientVersion.v_1_12_2)) {
                return;
            }
            final double deltaY = this.data.getPositionProcessor().getDeltaY();
            final double lastPosY = this.data.getPositionProcessor().getLastY();
            final boolean onGround = this.data.getPositionProcessor().isOnGround();
            final boolean step = mathOnGround(deltaY) && mathOnGround(lastPosY);
            final double expectedJumpMotion = 0.41999998688697815 + PlayerUtil.getPotionLevel(this.data.getPlayer(), PotionEffectType.JUMP) * 0.1f;
            final double maxHighJump = 0.41999998688697815 + PlayerUtil.getPotionLevel(this.data.getPlayer(), PotionEffectType.JUMP) * 0.1f + ((this.data.getVelocityProcessor().getTicksSinceVelocity() < 5) ? ((this.data.getVelocityProcessor().getVelocityY() > 0.0) ? this.data.getVelocityProcessor().getVelocityY() : 0.0) : 0.0);
            final boolean jumped = deltaY > 0.0 && lastPosY % 0.015625 == 0.0 && !onGround && !step;
            final boolean exempt = this.isExempt(ExemptType.BOAT, ExemptType.CLIMBABLE, ExemptType.CREATIVE, ExemptType.SLIME_ON_TICK, ExemptType.LAGGINGHARD, ExemptType.LAGGING, ExemptType.UNDERBLOCKWAS, ExemptType.VEHICLE, ExemptType.FLYING, ExemptType.SLIME, ExemptType.UNDERBLOCK, ExemptType.PISTON, ExemptType.LIQUID, ExemptType.BOAT, ExemptType.TELEPORT, ExemptType.WEB);
            this.debug("deltaY" + deltaY + " ejm=" + expectedJumpMotion + " mhj=" + maxHighJump + " step=" + step);
            if(this.isExempt(ExemptType.NEARSLIME) && String.format("%.2f", deltaY).equals("1.00")) {
                return;
            }
            if (jumped && !exempt && !this.isExempt(ExemptType.VELOCITY) && deltaY < expectedJumpMotion) {
                if(deltaY > 0.36 && deltaY < 0.37) {
                    return;
                }
                this.fail(String.format("ASC-L: %.2f < %.2f", deltaY, expectedJumpMotion));
            }
            if (!exempt && !step && deltaY > (this.data.getPositionProcessor().isOnGround() ? 0.6 : maxHighJump)) {
                this.fail(String.format("ASC-H: %.2f > %.2f", deltaY, expectedJumpMotion));
            }
            if (step && !this.isExempt(ExemptType.TELEPORT, ExemptType.JOINED) && deltaY > 0.6000000238418579) {
                this.fail(String.format("ASC-S: %.2f > 0.6", deltaY));
            }
        }
    }

    public static boolean mathOnGround(final double posY) {
        return posY % 0.015625 == 0.0;
    }
}
