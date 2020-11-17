package me.tecnio.antihaxerman.check.impl.autoblock;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "AutoBlock", type = "B")
public final class AutoBlockB extends Check {
    public AutoBlockB(PlayerData data) {
        super(data);
    }

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
            final boolean invalid = data.isBlocking() && data.getPlayer().isBlocking();

            if (invalid) {
                flag();
            }
        }
    }
}
