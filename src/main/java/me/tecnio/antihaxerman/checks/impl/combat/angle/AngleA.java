package me.tecnio.antihaxerman.checks.impl.combat.angle;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@CheckInfo(name = "Angle", type = "A")
public final class AngleA extends Check {
    public AngleA(PlayerData data) {
        super(data);
    }

    private Entity entity;

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (PacketType.Client.Util.isInstanceOfFlying(e.getPacketId())){
            if (entity != null){
                final Vector vec = entity.getLocation().clone().toVector().setY(0.0).subtract(data.getPlayer().getEyeLocation().clone().toVector().setY(0.0));
                final float angle = data.getPlayer().getEyeLocation().getDirection().angle(vec);

                if(angle > 1.3 && data.getPlayer().getLocation().toVector().setY(0.0).distance(entity.getLocation().toVector().setY(0.0)) > 1.0) {
                    if(++preVL > 2) {
                        flag(data, "angle = " + angle + ", rotation = " + data.getDeltaYaw());
                    }
                }else preVL = 0;
                entity = null;
            }
        }else if (e.getPacketId() == PacketType.Client.USE_ENTITY){ entity = new WrappedPacketInUseEntity(e.getNMSPacket()).getEntity(); }
    }
}
