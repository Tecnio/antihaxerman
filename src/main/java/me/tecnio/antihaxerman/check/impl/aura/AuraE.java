package me.tecnio.antihaxerman.check.impl.aura;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;

@CheckInfo(name = "Aura", type = "E")
public final class AuraE extends Check {
    public AuraE(PlayerData data) {
        super(data);
    }

    /*
     * Credits to Elevated https://github.com/ElevatedDev/Frequency
     */

    private int movements = 0, lastMovements = 0, total = 0;

    @Override
    public void onAttack(WrappedPacketInUseEntity wrapper) {
        if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
            final boolean proper = data.getCps() > 7.2 && movements < 4 && lastMovements < 4;

            if (proper) {
                final boolean flag = movements == lastMovements;

                if (flag) {
                    increaseBuffer();
                }

                if (++total == 30) {

                    if (buffer > 28)
                        flag();

                    total = 0;
                }
            }

            lastMovements = movements;
            movements = 0;
        }
    }
}
