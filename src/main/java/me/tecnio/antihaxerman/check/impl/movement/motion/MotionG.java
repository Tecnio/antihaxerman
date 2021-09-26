package me.tecnio.antihaxerman.check.impl.movement.motion;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.GameMode;

@CheckInfo(name = "Motion", type = "G", description = "Checks if player has vertical acceleration while in web.")
public class MotionG extends Check {

    public MotionG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying() && (data.getPlayer().getGameMode() == GameMode.SURVIVAL || data.getPlayer().getGameMode() == GameMode.ADVENTURE)) {
            final double acceleration = data.getPositionProcessor().getDeltaY();
            debug(isExempt(ExemptType.WEBRN) + " ACC: " + acceleration);
            if(isExempt(ExemptType.JOINED, ExemptType.COMBAT, ExemptType.UPWARDS_VEL, ExemptType.PEARL)) {
                return;
            }
            if(!isExempt(ExemptType.WEBRN)) {
                return;
            }
            boolean invalid = acceleration > 0.0209999998 && data.getPositionProcessor().getWebTicks() > 3 && !data.getPositionProcessor().isLastOnGround();
            if(invalid) {
                fail("DeltaY: " + acceleration);
            }
        }
    }
}
