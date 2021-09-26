package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@CheckInfo(name = "Aim", type = "M", description = "Checks if delta of yaw is invalid.")
public class AimM extends Check {
    public AimM(PlayerData data) {
        super(data);
    }

    private double lastDeltaYaw;


    @Override
    public void handle(Packet packet) {
        if(packet.isUseEntity()) {
            WrappedPacketInUseEntity useEntityPacket = new WrappedPacketInUseEntity(packet.getRawPacket());

            if (useEntityPacket.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {

                double yaw = MathUtil.wrapAngleTo180_float(data.getRotationProcessor().getDeltaYaw());

                double difference = Math.abs(yaw - lastDeltaYaw);

                if (difference == 0 && yaw > 2.0 && lastDeltaYaw > 2.0) {
                    if (increaseBuffer() > 2) {
                        fail();
                    }
                } else {
                    decreaseBuffer();
                }


                lastDeltaYaw = yaw;
            }
        }
    }
}
