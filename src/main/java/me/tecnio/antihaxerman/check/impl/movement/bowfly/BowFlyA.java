package me.tecnio.antihaxerman.check.impl.movement.bowfly;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.listener.bukkit.BukkitEventManager;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "BowFly", type = "A", description = "Checks if player is using bow to fly.")
public class BowFlyA extends Check {

    public boolean isHighPitch, wasHighPitch;

    public BowFlyA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isFlying() || packet.isPosition() || packet.isPosLook()) {
            WrappedPacketInFlying wrapped= new WrappedPacketInFlying(packet.getRawPacket());
            if(wrapped.getPitch() <= 0) {
                isHighPitch = true;
            }
            else {
                if(wasHighPitch) {
                    wasHighPitch = false;
                }
                if(isHighPitch) {
                    wasHighPitch = true;
                }
                isHighPitch = false;
            }
            boolean invalid = data.getPositionProcessor().getDeltaXZ() > 0.8 && !wrapped.isOnGround() && BukkitEventManager.selfdmg2.contains(data.getPlayer().getUniqueId());
            debug(data.getPositionProcessor().getDeltaXZ());
            if(invalid) {
                if(wasHighPitch || isHighPitch) {
                    fail("DeltaXZ: " + String.format("%.2f", data.getPositionProcessor().getDeltaXZ()) + "DELTAY: " + String.format("%.2f", data.getPositionProcessor().getDeltaY()))   ;
                }
            }
        }
    }
}
