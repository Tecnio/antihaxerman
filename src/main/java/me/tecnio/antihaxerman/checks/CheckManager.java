package me.tecnio.antihaxerman.checks;

import me.tecnio.antihaxerman.checks.impl.combat.aim.AimA;
import me.tecnio.antihaxerman.checks.impl.combat.angle.AngleA;
import me.tecnio.antihaxerman.checks.impl.combat.aura.*;
import me.tecnio.antihaxerman.checks.impl.combat.autoblock.AutoBlockA;
import me.tecnio.antihaxerman.checks.impl.combat.autoclicker.AutoClickerA;
import me.tecnio.antihaxerman.checks.impl.combat.autoclicker.AutoClickerB;
import me.tecnio.antihaxerman.checks.impl.combat.criticals.CriticalsA;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckManager {

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
            CriticalsA.class,
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

    public static List<Check> loadChecks() {
        List<Check> checklist = new ArrayList<>();
        Arrays.asList(checks).forEach(check -> {
            try {
                checklist.add((Check) check.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return checklist;
    }
}
