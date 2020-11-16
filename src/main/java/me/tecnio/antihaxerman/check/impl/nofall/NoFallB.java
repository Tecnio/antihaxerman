package me.tecnio.antihaxerman.check.impl.nofall;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;

@CheckInfo(name = "NoFall", type = "B")
public final class NoFallB extends Check {
    public NoFallB(PlayerData data) {
        super(data);
    }

    private int ticksSinceInVehicle;
    private double lastY;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (PacketUtils.isFlyingPacket(event.getPacketId())) {
            if (data.getPlayer().isInsideVehicle()) {
                ticksSinceInVehicle = 0;
            } else {
                ++ticksSinceInVehicle;
            }

            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(event.getNMSPacket());

            // Might have fixed a concurrency issue
            final double y = wrapper.isPosition() ? wrapper.getY() : data.getLocation().getY();

            final boolean clientOnGround = wrapper.isOnGround();
            final boolean serverOnGround = y % 0.015625 == 0.0 && lastY % 0.015625 == 0.0;

            final boolean exempt = data.liquidTicks() < 10 || data.pistonTicks() < 10 || data.climbableTicks() < 10 || ticksSinceInVehicle < 10 || data.isNearBoat();

            if (serverOnGround != clientOnGround && !exempt) {
                if (increaseBuffer() > 10) {
                    flag();
                }
            } else {
                resetBuffer();
            }

            lastY = y;
        }
    }
}
