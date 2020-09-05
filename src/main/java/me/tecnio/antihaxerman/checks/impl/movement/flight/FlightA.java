package me.tecnio.antihaxerman.checks.impl.movement.flight;

import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.checks.SetBackType;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import me.tecnio.antihaxerman.utils.PlayerUtils;

@CheckInfo(name = "Flight", type = "A")
public class FlightA extends Check {
    @Override
    public void onMove(PlayerData data) {
        double predicted = (data.getLastDeltaY() - 0.08) * 0.9800000190734863;

        if (!data.isOnGround() && data.getAirTicks() > 6 && !data.isTakingVelocity()){
            if (!PlayerUtils.inLiquid(data)
                    && !PlayerUtils.isInWeb(data)
                    && !PlayerUtils.isOnClimbable(data)
                    && data.getPlayer().getVelocity().getY() < -0.075D
                    && !data.getPlayer().isInsideVehicle()
                    && data.teleportTicks() > 10){
                double diff = Math.abs(data.getDeltaY() - predicted);
                if (diff >= 0.001 && Math.abs(predicted) >= 0.005){
                    if (++preVL > 5){
                        flag(data, "invalid vertical movement. diff: " + diff, SetBackType.PULLDOWN);
                    }
                }else preVL = 0;
            }
        }
    }
}
