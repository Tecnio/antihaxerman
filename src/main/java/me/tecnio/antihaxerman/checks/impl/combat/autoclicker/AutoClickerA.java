package me.tecnio.antihaxerman.checks.impl.combat.autoclicker;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import me.tecnio.antihaxerman.Config;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "AutoClicker", type = "A")
public final class AutoClickerA extends Check {

    private int flyingTicks;
    private double clicksPerSecond;

    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if (e.getPacketId() == PacketType.Client.ARM_ANIMATION){
            if (!data.isDigging() && flyingTicks <= 10) {
                final double speed = 1000 / ((flyingTicks * 50.0) > 0 ? (flyingTicks * 50.0) : 50);
                clicksPerSecond = ((clicksPerSecond * 19) + speed) / 20;

                data.setCps((int) clicksPerSecond);

                if (clicksPerSecond >= Config.MAX_CPS) {
                    flag(data, "cps = " + clicksPerSecond);
                }
            }
            flyingTicks = 0;
        }else if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())){
            flyingTicks++;
        }
    }
}
