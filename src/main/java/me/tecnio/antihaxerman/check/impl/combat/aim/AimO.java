package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Aim", type = "O", description = "Detects invalid yaw movements.")
public class AimO extends Check {

    private double lastSTD;
    private double lastDeltaYaw;
    private List<Double> deltaYawList = new ArrayList<>();

    public AimO(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isPosition()) {
            double yaw = MathUtil.wrapAngleTo180_float(data.getRotationProcessor().getYaw());

            if (yaw > 1.0) {
                deltaYawList.add(yaw);

                if (deltaYawList.size() >= 25) {
                    double std = MathUtil.getStandardDeviation(deltaYawList);


                    if (std < 0.03 || Math.abs(std - lastSTD) < 0.001) {
                        fail("STD: " + std);
                    }


                    lastSTD = std;
                    deltaYawList.clear();
                }
            }


            lastDeltaYaw = yaw;
        }
    }
}
