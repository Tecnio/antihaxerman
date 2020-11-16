package me.tecnio.antihaxerman.check.impl.badpackets;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.steervehicle.WrappedPacketInSteerVehicle;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "BadPackets", type = "E", maxVL = 1)
public final class BadPacketsE extends Check {
    public BadPacketsE(PlayerData data) {
        super(data);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketId() == PacketType.Client.STEER_VEHICLE) {
            final WrappedPacketInSteerVehicle wrapper = new WrappedPacketInSteerVehicle(event.getNMSPacket());

            if (data.getPlayer().getVehicle() == null) {
                flag();
            }

            final float forward = wrapper.getForwardValue();
            final float sideways = wrapper.getSideValue();

            if (forward != 0.0F && forward != 0.98F) {
                flag();
            }

            if (sideways != 0.0F && sideways != 0.98F) {
                flag();
            }
        }
    }
}
