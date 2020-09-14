package me.tecnio.antihaxerman.checks.impl.combat.aura;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.entity.Player;

@CheckInfo(name = "Aura", type = "C")
public final class AuraC extends Check {

    private int hitTicks;

    @Override
    public void onPacketReceive(PacketReceiveEvent e, PlayerData data) {
        if (e.getPacketId() == PacketType.Client.USE_ENTITY) {
            final WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(e.getNMSPacket());

            if (wrappedPacketInUseEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK && wrappedPacketInUseEntity.getEntity() instanceof Player) hitTicks = 0;

        } else if (isFlyingPacket(e)) {
            if (++hitTicks < 2 && data.isSprinting() && data.getDeltaXZ() > 0.1) {
                final double accel = Math.abs(data.getDeltaXZ() - data.getLastDeltaXZ());

                if (accel < 0.027) {
                    if (++preVL > 5) {
                        flag(data, "keepsprint. accel: " + accel);
                    }
                } else preVL = 0;
            }
        }
    }
}
