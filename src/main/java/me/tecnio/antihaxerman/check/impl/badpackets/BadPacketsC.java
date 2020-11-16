package me.tecnio.antihaxerman.check.impl.badpackets;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.entity.Entity;

@CheckInfo(name = "BadPackets", type = "C", maxVL = 1)
public final class BadPacketsC extends Check {
    public BadPacketsC(PlayerData data) {
        super(data);
    }

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        final Entity target = wrapper.getEntity();

        if (target != null) {
            if (target == data.getPlayer()) {
                flag();
            }
        }
    }
}
