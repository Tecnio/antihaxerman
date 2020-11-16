package me.tecnio.antihaxerman.check.impl.nofall;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packetwrappers.in.flying.WrappedPacketInFlying;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.utils.packet.PacketUtils;
import me.tecnio.antihaxerman.utils.player.CollisionUtils;

@CheckInfo(name = "NoFall", type = "A")
public final class NoFallA extends Check {
    public NoFallA(PlayerData data) {
        super(data);
    }

    private int ticksSinceInVehicle;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (PacketUtils.isFlyingPacket(event.getPacketId())) {
            if (data.getPlayer().isInsideVehicle()) {
                ticksSinceInVehicle = 0;
            } else {
                ++ticksSinceInVehicle;
            }

            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(event.getNMSPacket());

            final boolean clientOnGround = wrapper.isOnGround();
            final boolean serverOnGround = CollisionUtils.isOnGround(data);

            final boolean exempt = (data.getLastFlying() - data.getLastLastFlying()) < 1 || data.isLagging() || !data.isTouchingAir() || data.liquidTicks() < 10 || data.climbableTicks() < 10 || ticksSinceInVehicle < 10 || data.isNearBoat();

            if (serverOnGround != clientOnGround && !exempt) {
                if (increaseBuffer() > 15) {
                    flag();
                }
            } else {
                resetBuffer();
            }
        }
    }
}
