package me.tecnio.antihaxerman.checks.impl.combat.aura;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.entity.Entity;

@CheckInfo(name = "Aura", type = "D")
public class AuraD extends Check {

    private int ticks;
    private Entity lastTarget;

    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if(e.getPacketId() == PacketType.Client.USE_ENTITY) {
            WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(e.getNMSPacket());

            if(wrappedPacketInUseEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                Entity target = wrappedPacketInUseEntity.getEntity();

                if(target != lastTarget) {
                    if((data.getDeltaYaw() == 0.0 && ticks <= 5) || (data.getDeltaYaw() > 5 && ticks < 2)) {
                        if(++preVL > 1) {
                            flag(data, "rot = " + data.getDeltaYaw() + ", ticks = " + ticks);
                        }
                    }else preVL = 0;
                }

                ticks = 0;
                lastTarget = target;
            }
        }else if(PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())) {
            ticks++;
        }
    }
}
