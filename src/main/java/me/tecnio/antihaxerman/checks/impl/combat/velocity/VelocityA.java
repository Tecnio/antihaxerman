package me.tecnio.antihaxerman.checks.impl.combat.velocity;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "Velocity", type = "A")
public class VelocityA extends Check {
    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if (isFlyingPacket(e)) {
            if (data.getVelocityTicks() == 1) {
                double velTaken = data.getDeltaY();
                double velExpected = data.getLastVel().getY() * 0.99F;
                double percentage = (velTaken * 100) / velExpected;

                if (velTaken <= velExpected
                        && data.liquidTicks() > 20
                        && !data.isOnClimbableBlock()
                        && data.underBlockTicks() > 20
                        && !data.isNearWall()) {
                    if (++preVL > 2) {
                        flag(data, "took less velocity than expected. p: " + percentage);
                    }
                }else preVL = 0;
            }
        }
    }
}
