package me.tecnio.antihaxerman.check.impl.aura;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Aura", type = "B")
public final class AuraB extends Check {
    public AuraB(PlayerData data) {
        super(data);
    }

    private int lastAttackedEntityID;

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
            if (wrapper.getEntityID() != lastAttackedEntityID) {
                if (increaseBuffer() > 1) {
                    flag();
                }
            }
        }
        lastAttackedEntityID = wrapper.getEntityID();
    }

    @Override
    public void onFlying() {
        resetBuffer();
    }
}
