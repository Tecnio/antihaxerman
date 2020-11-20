/*
 * Copyright (C) 2020 Tecnio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.check;

import lombok.Getter;
import me.tecnio.antihaxerman.check.impl.aim.*;
import me.tecnio.antihaxerman.check.impl.angle.AngleA;
import me.tecnio.antihaxerman.check.impl.aura.*;
import me.tecnio.antihaxerman.check.impl.autoblock.AutoBlockA;
import me.tecnio.antihaxerman.check.impl.autoblock.AutoBlockB;
import me.tecnio.antihaxerman.check.impl.autoclicker.AutoClickerA;
import me.tecnio.antihaxerman.check.impl.autoclicker.AutoClickerB;
import me.tecnio.antihaxerman.check.impl.autoclicker.AutoClickerC;
import me.tecnio.antihaxerman.check.impl.badpackets.*;
import me.tecnio.antihaxerman.check.impl.fastclimb.FastClimbA;
import me.tecnio.antihaxerman.check.impl.fastclimb.FastClimbB;
import me.tecnio.antihaxerman.check.impl.flight.FlightA;
import me.tecnio.antihaxerman.check.impl.flight.FlightB;
import me.tecnio.antihaxerman.check.impl.flight.FlightC;
import me.tecnio.antihaxerman.check.impl.inventory.InventoryA;
import me.tecnio.antihaxerman.check.impl.invmove.InvMoveA;
import me.tecnio.antihaxerman.check.impl.largemove.LargeMoveA;
import me.tecnio.antihaxerman.check.impl.motion.MotionA;
import me.tecnio.antihaxerman.check.impl.motion.MotionB;
import me.tecnio.antihaxerman.check.impl.motion.MotionC;
import me.tecnio.antihaxerman.check.impl.nofall.NoFallA;
import me.tecnio.antihaxerman.check.impl.nofall.NoFallB;
import me.tecnio.antihaxerman.check.impl.noslowdown.NoSlowDownA;
import me.tecnio.antihaxerman.check.impl.noslowdown.NoSlowDownB;
import me.tecnio.antihaxerman.check.impl.omnisprint.OmniSprintA;
import me.tecnio.antihaxerman.check.impl.pingspoof.PingSpoofA;
import me.tecnio.antihaxerman.check.impl.post.*;
import me.tecnio.antihaxerman.check.impl.reach.ReachA;
import me.tecnio.antihaxerman.check.impl.speed.*;
import me.tecnio.antihaxerman.check.impl.timer.TimerA;
import me.tecnio.antihaxerman.check.impl.velocity.VelocityA;
import me.tecnio.antihaxerman.check.impl.velocity.VelocityB;
import me.tecnio.antihaxerman.data.CinematicProcessor;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.data.SensitivityProcessor;

import java.util.ArrayList;
import java.util.List;

public final class CheckManager {

    /*
    * Credits to GladUrBad I did the system he improved it.
     */

    @Getter
    public static final Class<?>[] CHECKS = new Class<?>[]{
            AuraA.class,
            AuraB.class,
            AuraC.class,
            AuraD.class,
            AuraE.class,
            AimA.class,
            AimB.class,
            AimC.class,
            AimD.class,
            AimE.class,
            ReachA.class,
            AutoClickerA.class,
            AutoClickerB.class,
            AutoClickerC.class,
            AutoBlockA.class,
            AutoBlockB.class,
            VelocityA.class,
            VelocityB.class,
            AngleA.class,
            FlightA.class,
            FlightB.class,
            FlightC.class,
            SpeedA.class,
            SpeedB.class,
            SpeedC.class,
            SpeedD.class,
            SpeedE.class,
            NoSlowDownA.class,
            NoSlowDownB.class,
            InvMoveA.class,
            InventoryA.class,
            FastClimbA.class,
            FastClimbB.class,
            OmniSprintA.class,
            LargeMoveA.class,
            MotionA.class,
            MotionB.class,
            MotionC.class,
            TimerA.class,
            NoFallA.class,
            NoFallB.class,
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsD.class,
            BadPacketsE.class,
            BadPacketsF.class,
            BadPacketsG.class,
            PostA.class,
            PostB.class,
            PostC.class,
            PostD.class,
            PostE.class,
            PingSpoofA.class,

            CinematicProcessor.class,
            SensitivityProcessor.class,
    };

    public static List<Check> loadChecks(PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (Class<?> c : CHECKS) {
            try {
                checkList.add((Check) c.getConstructor(PlayerData.class).newInstance(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return checkList;
    }
}
