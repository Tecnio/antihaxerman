package me.tecnio.antihaxerman.checks.impl.combat.velocity;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "Velocity", type = "B")
public final class VelocityB extends Check {
    public VelocityB(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (isFlyingPacket(e)) {
            if (data.velocityTicks() == 1) {
                if (data.liquidTicks() > 20 && !data.isOnClimbableBlock() && data.underBlockTicks() > 20 && !data.isNearWall()) {
                    //Calculate the expected vel.
                    final double expectedVelX = data.getLastVelocity().getX() * (data.attackTicks() < 2 ? 0.6 : 1.0);
                    final double expectedVelZ = data.getLastVelocity().getZ() * (data.attackTicks() < 2 ? 0.6 : 1.0);
                    final double expectedHorizontalVel = Math.hypot(expectedVelX, expectedVelZ);

                    //Get the current horizontal vel.
                    final double velTaken = data.getDeltaXZ();

                    //The normal ammount should be close to 100. Any abnormal differences are a blatant velocity modifier.
                    final double percentage = (100.0 * velTaken) / expectedHorizontalVel;

                    if (percentage < 60 || percentage > 200) {
                        if (++buffer > 1) {
                            flag(data, "took abnormal amount of velocity. p: " + percentage);
                        }
                    } else buffer = 0;
                }
            }
        }
    }
}
