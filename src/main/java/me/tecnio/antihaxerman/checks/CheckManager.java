package me.tecnio.antihaxerman.checks;

import me.tecnio.antihaxerman.checks.impl.combat.aim.AimA;
import me.tecnio.antihaxerman.checks.impl.combat.angle.AngleA;
import me.tecnio.antihaxerman.checks.impl.combat.aura.*;
import me.tecnio.antihaxerman.checks.impl.combat.autoblock.AutoBlockA;
import me.tecnio.antihaxerman.checks.impl.combat.autoclicker.AutoClickerA;
import me.tecnio.antihaxerman.checks.impl.combat.autoclicker.AutoClickerB;
import me.tecnio.antihaxerman.checks.impl.combat.fastbow.FastBowA;
import me.tecnio.antihaxerman.checks.impl.combat.reach.ReachA;
import me.tecnio.antihaxerman.checks.impl.combat.velocity.VelocityA;
import me.tecnio.antihaxerman.checks.impl.movement.fastclimb.FastClimbA;
import me.tecnio.antihaxerman.checks.impl.movement.fastclimb.FastClimbB;
import me.tecnio.antihaxerman.checks.impl.movement.flight.FlightA;
import me.tecnio.antihaxerman.checks.impl.movement.flight.FlightB;
import me.tecnio.antihaxerman.checks.impl.movement.invalid.InvalidA;
import me.tecnio.antihaxerman.checks.impl.movement.motion.MotionA;
import me.tecnio.antihaxerman.checks.impl.movement.motion.MotionB;
import me.tecnio.antihaxerman.checks.impl.movement.motion.MotionC;
import me.tecnio.antihaxerman.checks.impl.movement.motion.MotionD;
import me.tecnio.antihaxerman.checks.impl.movement.noslow.NoSlowA;
import me.tecnio.antihaxerman.checks.impl.movement.scaffold.ScaffoldA;
import me.tecnio.antihaxerman.checks.impl.movement.scaffold.ScaffoldB;
import me.tecnio.antihaxerman.checks.impl.movement.speed.SpeedA;
import me.tecnio.antihaxerman.checks.impl.movement.speed.SpeedB;
import me.tecnio.antihaxerman.checks.impl.movement.speed.SpeedC;
import me.tecnio.antihaxerman.checks.impl.movement.sprint.SprintA;
import me.tecnio.antihaxerman.checks.impl.player.badpackets.*;
import me.tecnio.antihaxerman.checks.impl.player.invmove.InvMoveA;
import me.tecnio.antihaxerman.checks.impl.player.nofall.NoFallA;
import me.tecnio.antihaxerman.checks.impl.player.timer.TimerA;
import me.tecnio.antihaxerman.playerdata.PlayerData;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public final class CheckManager {

    public static final Class[] checks = new Class[]{
            AimA.class,
            AuraA.class,
            AuraB.class,
            AuraC.class,
            AuraD.class,
            AuraE.class,
            AutoClickerA.class,
            AutoClickerB.class,
            AutoBlockA.class,
            AngleA.class,
            ReachA.class,
            VelocityA.class,
            FastBowA.class,
            SpeedA.class,
            SpeedB.class,
            SpeedC.class,
            FlightA.class,
            FlightB.class,
            FastClimbA.class,
            FastClimbB.class,
            SprintA.class,
            NoSlowA.class,
            MotionA.class,
            MotionB.class,
            MotionC.class,
            MotionD.class,
            InvMoveA.class,
            ScaffoldA.class,
            ScaffoldB.class,
            InvalidA.class,
            TimerA.class,
            NoFallA.class,
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsD.class,
            BadPacketsE.class,
    };

    private static final List<Constructor<?>> CONSTRUCTORS = new ArrayList<>();

    public static void registerChecks() {
        for (Class clazz : checks) {
            try {
                CONSTRUCTORS.add(clazz.getConstructor(PlayerData.class));
            } catch (NoSuchMethodException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static List<Check> loadChecks(PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (Constructor<?> constructor : CONSTRUCTORS) {
            try {
                checkList.add((Check) constructor.newInstance(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return checkList;
    }
}
