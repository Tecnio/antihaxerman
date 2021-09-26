package me.tecnio.antihaxerman.manager;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.impl.combat.aim.*;
import me.tecnio.antihaxerman.check.impl.combat.angle.AngleA;
import me.tecnio.antihaxerman.check.impl.combat.aura.*;
import me.tecnio.antihaxerman.check.impl.combat.autoblock.AutoBlockA;
import me.tecnio.antihaxerman.check.impl.combat.autoblock.AutoBlockB;
import me.tecnio.antihaxerman.check.impl.combat.autoblock.AutoBlockC;
import me.tecnio.antihaxerman.check.impl.combat.autoclicker.*;
import me.tecnio.antihaxerman.check.impl.combat.hitbox.HitboxA;
import me.tecnio.antihaxerman.check.impl.combat.reach.ReachA;
import me.tecnio.antihaxerman.check.impl.combat.reach.ReachB;
import me.tecnio.antihaxerman.check.impl.combat.reach.ReachC;
import me.tecnio.antihaxerman.check.impl.combat.velocity.VelocityA;
import me.tecnio.antihaxerman.check.impl.combat.velocity.VelocityB;
import me.tecnio.antihaxerman.check.impl.combat.velocity.VelocityC;
import me.tecnio.antihaxerman.check.impl.movement.bowfly.BowFlyA;
import me.tecnio.antihaxerman.check.impl.movement.fastclimb.FastClimbA;
import me.tecnio.antihaxerman.check.impl.movement.fastclimb.FastClimbB;
import me.tecnio.antihaxerman.check.impl.movement.flight.*;
import me.tecnio.antihaxerman.check.impl.movement.jesus.JesusA;
import me.tecnio.antihaxerman.check.impl.movement.jesus.JesusB;
import me.tecnio.antihaxerman.check.impl.movement.jesus.JesusC;
import me.tecnio.antihaxerman.check.impl.movement.largemove.LargeMoveA;
import me.tecnio.antihaxerman.check.impl.movement.largemove.LargeMoveB;
import me.tecnio.antihaxerman.check.impl.movement.liquidspeed.LiquidSpeedA;
import me.tecnio.antihaxerman.check.impl.movement.liquidspeed.LiquidSpeedB;
import me.tecnio.antihaxerman.check.impl.movement.liquidspeed.LiquidSpeedC;
import me.tecnio.antihaxerman.check.impl.movement.motion.*;
import me.tecnio.antihaxerman.check.impl.movement.noslow.NoSlowA;
import me.tecnio.antihaxerman.check.impl.movement.noslow.NoSlowB;
import me.tecnio.antihaxerman.check.impl.movement.noslow.NoSlowC;
import me.tecnio.antihaxerman.check.impl.movement.noslow.NoSlowD;
import me.tecnio.antihaxerman.check.impl.movement.omnisprint.OmniSprintA;
import me.tecnio.antihaxerman.check.impl.movement.speed.*;
import me.tecnio.antihaxerman.check.impl.movement.strafe.StrafeA;
import me.tecnio.antihaxerman.check.impl.player.anticactus.AntiCactusA;
import me.tecnio.antihaxerman.check.impl.player.badpackets.*;
import me.tecnio.antihaxerman.check.impl.player.breaker.BreakerA;
import me.tecnio.antihaxerman.check.impl.player.fastplace.FastPlaceA;
import me.tecnio.antihaxerman.check.impl.player.groundspoof.GroundSpoofA;
import me.tecnio.antihaxerman.check.impl.player.groundspoof.GroundSpoofB;
import me.tecnio.antihaxerman.check.impl.player.groundspoof.GroundSpoofC;
import me.tecnio.antihaxerman.check.impl.player.groundspoof.GroundSpoofD;
import me.tecnio.antihaxerman.check.impl.player.inventory.*;
import me.tecnio.antihaxerman.check.impl.player.pingspoof.PingSpoofA;
import me.tecnio.antihaxerman.check.impl.player.post.*;
import me.tecnio.antihaxerman.check.impl.player.scaffold.*;
import me.tecnio.antihaxerman.check.impl.player.timer.*;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CheckManager {

    public static final Class<?>[] CHECKS = new Class[]{
            AimA.class,
            AimB.class,
            AimC.class,
            AimD.class,
            AimE.class,
            AimF.class,
            AimG.class,
            AimH.class,
            AimI.class,
            AimJ.class,
            AimK.class,
            AimL.class,
            AimM.class,
            AimN.class,
            AimO.class,
            AuraAA.class,
            AuraA.class,
            AuraB.class,
            AuraC.class,
            AuraD.class,
            AuraE.class,
            AuraF.class,
            AuraG.class,
            AutoClickerF.class,
            AutoClickerA.class,
            AutoClickerB.class,
            AutoClickerC.class,
            AutoClickerD.class,
            AutoClickerE.class,
            AutoBlockA.class,
            AutoBlockB.class,
            AutoBlockC.class,
            ReachA.class,
            ReachB.class,
            ReachC.class,
            HitboxA.class,
            AngleA.class,
            VelocityA.class,
            VelocityB.class,
            VelocityC.class,
            BowFlyA.class,
            SpeedA.class,
            SpeedB.class,
            SpeedC.class,
            SpeedD.class,
            SpeedE.class,
            SpeedG.class,
            FlightA.class,
            FlightB.class,
            FlightC.class,
            FlightD.class,
            FlightE.class,
            StrafeA.class,
            MotionA.class,
            MotionB.class,
            MotionC.class,
            MotionD.class,
            MotionE.class,
            MotionE.class,
            MotionF.class,
            MotionG.class,
            MotionH.class,
            NoSlowA.class,
            NoSlowB.class,
            NoSlowC.class,
            NoSlowD.class,
            OmniSprintA.class,
            LargeMoveA.class,
            LargeMoveB.class,
            FastClimbA.class,
            FastClimbB.class,
            JesusA.class,
            JesusB.class,
            JesusC.class,
            LiquidSpeedA.class,
            LiquidSpeedB.class,
            LiquidSpeedC.class,
            GroundSpoofA.class,
            GroundSpoofB.class,
            GroundSpoofC.class,
            GroundSpoofD.class,
            TimerA.class,
            TimerB.class,
            TimerC.class,
            TimerD.class,
            TimerE.class,
            PostA.class,
            PostB.class,
            PostC.class,
            PostD.class,
            PostE.class,
            PostF.class,
            PostG.class,
            InventoryA.class,
            InventoryB.class,
            InventoryC.class,
            InventoryD.class,
            InventoryE.class,
            InventoryF.class,
            InventoryG.class,
            PingSpoofA.class,
            FastPlaceA.class,
            ScaffoldA.class,
            ScaffoldB.class,
            ScaffoldC.class,
            ScaffoldD.class,
            ScaffoldE.class,
            ScaffoldF.class,
            ScaffoldG.class,
            ScaffoldH.class,
            ScaffoldI.class,
            ScaffoldJ.class,
            ScaffoldK.class,
            ScaffoldL.class,
            ScaffoldM.class,
            AntiCactusA.class,
            BreakerA.class,
            BadPacketsA.class,
            BadPacketsB.class,
            BadPacketsC.class,
            BadPacketsE.class,
            BadPacketsF.class,
            BadPacketsG.class,
            BadPacketsH.class,
            BadPacketsI.class,
            BadPacketsJ.class,
            BadPacketsK.class,
            BadPacketsL.class,
            BadPacketsM.class,
            BadPacketsN.class,
            BadPacketsO.class,
            BadPacketsP.class
    };

    private static final List<Constructor<?>> CONSTRUCTORSALL = new ArrayList<>();

    public static List<Check> allChecks;

    public static List<Check> loadChecks(final PlayerData data) {
        final List<Check> checkList = new ArrayList<>();
        for (final Constructor<?> constructor : CONSTRUCTORSALL) {
            try {
                Check check = (Check) constructor.newInstance(data);
                check.setPunishCommands((ArrayList<String>) Config.PUNISH_COMMANDS.get(constructor.getClass().getSimpleName()));
                check.setEnabled(Config.ENABLED_CHECKS.stream().anyMatch(s -> s.equals(check.getClass().getSimpleName())));
                try {
                    check.setMaxVl(Config.MAX_VIOLATIONS.get(constructor.getClass().getSimpleName()));
                } catch(NullPointerException e) {
                    check.setMaxVl(50);
                }
                checkList.add(check);
            } catch (final Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        allChecks = checkList;
        return checkList;
    }

    public static Map<Check, Integer> loadChecksMap(final PlayerData data, List<Check> checks) {
        final Map<Check, Integer> checkList = new HashMap<>();
        for (final Constructor<?> constructor : CONSTRUCTORSALL) {
            try {
                if(checks.stream().anyMatch(check -> check.getFullName().equals(constructor.getName()))) {
                    Check check = checks.stream().filter(check1 -> check1.getFullName().equals(constructor.getName())).findFirst().get();
                    checkList.put(check, 0);
                }
            } catch (final Exception exception) {
                System.err.println("Failed to load checks for " + data.getPlayer().getName());
                exception.printStackTrace();
            }
        }
        return checkList;
    }
    public static void setup() {
        for (final Class<?> clazz : CHECKS) {
            if (Config.ENABLED_CHECKS.contains(clazz.getSimpleName())) {
                try {
                    CONSTRUCTORSALL.add(clazz.getConstructor(PlayerData.class));
                    Bukkit.getLogger().info(clazz.getSimpleName() + " is enabled!");
                } catch (final NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            } else {
                try {
                    CONSTRUCTORSALL.add(clazz.getConstructor(PlayerData.class));
                    Bukkit.getLogger().info(clazz.getSimpleName() + " is disabled!");
                } catch (final NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}

