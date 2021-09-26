package me.tecnio.antihaxerman.check.impl.player.scaffold;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Scaffold", type = "D", description = "Checks for invalid acceleration.")
public class ScaffoldD extends Check {

    private boolean placedBlock;


    public ScaffoldD(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying()) {
            if (placedBlock && isBridging()) {
                final double deltaXZ = data.getPositionProcessor().getDeltaXZ();
                final double lastDeltaXZ = data.getPositionProcessor().getLastDeltaXZ();

                final double accel = Math.abs(deltaXZ - lastDeltaXZ);

                final float deltaYaw = data.getRotationProcessor().getDeltaYaw() % 360F;
                final float deltaPitch = data.getRotationProcessor().getDeltaPitch();

                final boolean invalid = deltaYaw > 75F && deltaPitch > 15F && accel < 0.15;

                if (invalid) {
                    if (increaseBuffer() > 3) {
                        fail(String.format("accel=%.2f, deltaYaw=%.2f, deltaPitch=%.2f", accel, deltaYaw, deltaPitch));
                    }
                } else {
                    setBuffer(Math.max(getBuffer() - 0.5, 0));
                }
            }
            placedBlock = false;
        } else if (packet.isBlockPlace()) {
            if (data.getPlayer().getItemInHand().getType().isBlock()) {
                placedBlock = true;
            }
        }
    }
}
