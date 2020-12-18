/*
 *  Copyright (C) 2020 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
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

package me.tecnio.antihaxerman.manager;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.impl.combat.aim.AimA;
import me.tecnio.antihaxerman.check.impl.combat.aim.AimB;
import me.tecnio.antihaxerman.check.impl.combat.aim.AimC;
import me.tecnio.antihaxerman.check.impl.combat.aim.AimD;
import me.tecnio.antihaxerman.check.impl.combat.aura.AuraA;
import me.tecnio.antihaxerman.check.impl.combat.aura.AuraB;
import me.tecnio.antihaxerman.check.impl.combat.aura.AuraC;
import me.tecnio.antihaxerman.check.impl.combat.aura.AuraD;
import me.tecnio.antihaxerman.check.impl.combat.autoclicker.AutoClickerA;
import me.tecnio.antihaxerman.check.impl.combat.autoclicker.AutoClickerB;
import me.tecnio.antihaxerman.check.impl.movement.fastclimb.FastClimbA;
import me.tecnio.antihaxerman.check.impl.movement.flight.FlightA;
import me.tecnio.antihaxerman.check.impl.movement.flight.FlightB;
import me.tecnio.antihaxerman.check.impl.movement.flight.FlightC;
import me.tecnio.antihaxerman.check.impl.movement.flight.FlightD;
import me.tecnio.antihaxerman.check.impl.movement.largemove.LargeMoveA;
import me.tecnio.antihaxerman.check.impl.movement.largemove.LargeMoveB;
import me.tecnio.antihaxerman.check.impl.movement.liquidspeed.LiquidSpeedA;
import me.tecnio.antihaxerman.check.impl.movement.motion.MotionA;
import me.tecnio.antihaxerman.check.impl.movement.motion.MotionB;
import me.tecnio.antihaxerman.check.impl.movement.noslow.NoSlowA;
import me.tecnio.antihaxerman.check.impl.movement.speed.SpeedA;
import me.tecnio.antihaxerman.check.impl.movement.speed.SpeedB;
import me.tecnio.antihaxerman.check.impl.movement.speed.SpeedC;
import me.tecnio.antihaxerman.check.impl.player.badpackets.BadPacketsA;
import me.tecnio.antihaxerman.check.impl.player.badpackets.BadPacketsB;
import me.tecnio.antihaxerman.check.impl.player.badpackets.BadPacketsC;
import me.tecnio.antihaxerman.check.impl.player.badpackets.BadPacketsD;
import me.tecnio.antihaxerman.check.impl.player.groundspoof.GroundSpoofA;
import me.tecnio.antihaxerman.check.impl.player.groundspoof.GroundSpoofB;
import me.tecnio.antihaxerman.check.impl.player.groundspoof.GroundSpoofC;
import me.tecnio.antihaxerman.check.impl.player.post.*;
import me.tecnio.antihaxerman.check.impl.player.timer.TimerA;
import me.tecnio.antihaxerman.check.impl.player.timer.TimerB;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public final class CheckManager {

    public static final Class[] CHECKS = new Class[]{
            AimA.class,
            AimB.class,
            AimC.class,
            AimD.class,
            AuraA.class,
            AuraB.class,
            AuraC.class,
            AuraD.class,
            AutoClickerA.class,
            AutoClickerB.class,
            SpeedA.class,
            SpeedB.class,
            SpeedC.class,
            FlightA.class,
            FlightB.class,
            FlightC.class,
            FlightD.class,
            MotionA.class,
            MotionB.class,
            NoSlowA.class,
            LargeMoveA.class,
            LargeMoveB.class,
            FastClimbA.class,
            LiquidSpeedA.class,
            GroundSpoofA.class,
            GroundSpoofB.class,
            GroundSpoofC.class,
            TimerA.class,
            TimerB.class,
            PostA.class,
            PostB.class,
            PostC.class,
            PostD.class,
            PostE.class,
            PostF.class,
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsD.class,
    };

    private static final List<Constructor<?>> CONSTRUCTORS = new ArrayList<>();

    public static List<Check> loadChecks(PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (Constructor<?> constructor : CONSTRUCTORS) {
            try {
                checkList.add((Check) constructor.newInstance(data));
            } catch (Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        return checkList;
    }

    public static void setup() {
        for (Class clazz : CHECKS) {
            if (Config.ENABLED_CHECKS.contains(clazz.getSimpleName())) {
                try {
                    CONSTRUCTORS.add(clazz.getConstructor(PlayerData.class));
                    Bukkit.getLogger().info(clazz.getSimpleName() + " is enabled!");
                } catch (NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            } else {
                Bukkit.getLogger().info(clazz.getSimpleName() + " is disabled! " + Config.ENABLED_CHECKS.size());
            }
        }
    }
}

