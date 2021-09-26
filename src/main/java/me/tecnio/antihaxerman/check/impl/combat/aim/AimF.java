

package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "Aim", type = "F", description = "Checks for round yaw.")
public final class AimF extends Check {



    public AimF(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {
        if (packet.isPosLook()) {
            if ((System.currentTimeMillis() - data.getCombatProcessor().getLastUseEntityPacket()) < 1000L) {
                if (data.getCombatProcessor().getTarget() != null && data.getCombatProcessor().getTarget().getWorld().equals(data.getPlayer().getWorld()) && data.getPlayer().getLocation().distance(data.getCombatProcessor().getTarget().getLocation()) > 1.55) {
                    WrappedPacketInFlying WrappedPacketInFlying = new WrappedPacketInFlying(packet.getRawPacket());
                    if (Math.abs(WrappedPacketInFlying.getPitch()) == 90.0) return;
                    double yaw = WrappedPacketInFlying.getYaw(), pitch = WrappedPacketInFlying.getPitch();
                    double roundYaw1 = Math.round(yaw), roundPitch1 = Math.round(pitch);
                    double roundYaw2 = MathUtil.preciseRound(yaw, 1), roundPitch2 = MathUtil.preciseRound(pitch, 1);

                    if ((yaw == roundYaw1 || roundPitch1 == pitch || roundYaw2 == yaw || roundPitch2 == pitch) && increaseBuffer() > 3) {
                        fail("RoundPitch: " + roundYaw1 + " RoundYaw: " + roundYaw2);
                    }
                    else {
                        decreaseBufferBy(0.05);
                    }
                }
            }
        }
    }
}
