/*
 *  Copyright (C) 2020-2021 Tecnio
 *
 *  This check is different than others, you can't take it or include it in any other application/project.
 *
 *  The license may allow you to use this check but in this scenario the license is not effective.
 *  And for anyone who opposes claiming license is GPLv3 I clearly have written a different license here.
 *
 *  Be aware.
 */

package me.tecnio.antihaxerman.check.impl.combat.aim;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;

@CheckInfo(name = "Aim", type = "H", description = "L ez kot")
public final class AimH extends Check {

    // Read the license above
    private float lastDeltaYaw;

    public AimH(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final Packet packet) {

    }
}
