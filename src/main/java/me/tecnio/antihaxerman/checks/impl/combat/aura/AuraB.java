package me.tecnio.antihaxerman.checks.impl.combat.aura;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;

@CheckInfo(name = "Aura", type = "B")
public final class AuraB extends Check {

    private int ticks, lastAttackedEntityID;

    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())){
            ticks = 0;
        }else if (e.getPacketId() == PacketType.Client.USE_ENTITY){
            final WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(e.getNMSPacket());

            if (wrappedPacketInUseEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK){
                if (++ticks > 1 && wrappedPacketInUseEntity.getEntityId() != lastAttackedEntityID) flag(data, "attacked " + ticks + " entities in a single tick.");
            }

            lastAttackedEntityID = wrappedPacketInUseEntity.getEntityId();
        }
    }
}
