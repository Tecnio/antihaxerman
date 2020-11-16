package me.tecnio.antihaxerman.check.impl.autoclicker;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

@CheckInfo(name = "AutoClicker", type = "A")
public final class AutoClickerA extends Check {
    public AutoClickerA(PlayerData data) {
        super(data);
    }

    private int flyingTicks;
    private double clicksPerSecond;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.ARM_ANIMATION) {
            if (!data.isDigging() && flyingTicks <= 10) {
                final double speed = 1000 / ((flyingTicks * 50.0) > 0 ? (flyingTicks * 50.0) : 50);

                clicksPerSecond = ((clicksPerSecond * 19) + speed) / 20;

                if (clicksPerSecond >= 25) {
                    flag("cps = " + clicksPerSecond);
                }
            }

            flyingTicks = 0;
        } else if (PacketUtils.isFlyingPacket(event.getPacketId())) {
            flyingTicks++;
        }
    }
}
