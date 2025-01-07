package me.tecnio.ahm.check.api;

import lombok.Getter;
import lombok.SneakyThrows;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.impl.aim.AimA;
import me.tecnio.ahm.check.impl.aim.AimB;
import me.tecnio.ahm.check.impl.aura.AuraA;
import me.tecnio.ahm.check.impl.aura.AuraB;
import me.tecnio.ahm.check.impl.aura.AuraC;
import me.tecnio.ahm.check.impl.autoclicker.AutoClickerA;
import me.tecnio.ahm.check.impl.flight.FlightA;
import me.tecnio.ahm.check.impl.flight.FlightB;
import me.tecnio.ahm.check.impl.flight.FlightC;
import me.tecnio.ahm.check.impl.flight.FlightD;
import me.tecnio.ahm.check.impl.groundspoof.GroundSpoofA;
import me.tecnio.ahm.check.impl.hitbox.HitboxA;
import me.tecnio.ahm.check.impl.interact.InteractA;
import me.tecnio.ahm.check.impl.interact.InteractB;
import me.tecnio.ahm.check.impl.interact.InteractC;
import me.tecnio.ahm.check.impl.protocol.*;
import me.tecnio.ahm.check.impl.scaffold.ScaffoldA;
import me.tecnio.ahm.check.impl.speed.SpeedA;
import me.tecnio.ahm.check.impl.speed.SpeedB;
import me.tecnio.ahm.check.impl.timer.TimerA;
import me.tecnio.ahm.check.impl.velocity.VelocityA;
import me.tecnio.ahm.check.impl.velocity.VelocityB;
import me.tecnio.ahm.check.impl.velocity.VelocityC;
import me.tecnio.ahm.data.PlayerData;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class CheckManager {

    private final List<Class<?>> checks = new ArrayList<>();

    public CheckManager() {
        this.checks.add(AimA.class);
        this.checks.add(AimB.class);

        this.checks.add(AuraA.class);
        this.checks.add(AuraB.class);
        this.checks.add(AuraC.class);

        this.checks.add(AutoClickerA.class);

        this.checks.add(FlightA.class);
        this.checks.add(FlightB.class);
        this.checks.add(FlightC.class);
        this.checks.add(FlightD.class);

        this.checks.add(GroundSpoofA.class);

        this.checks.add(HitboxA.class);

        this.checks.add(InteractA.class);
        this.checks.add(InteractB.class);
        this.checks.add(InteractC.class);

        this.checks.add(ProtocolA.class);
        this.checks.add(ProtocolB.class);
        this.checks.add(ProtocolC.class);
        this.checks.add(ProtocolD.class);
        this.checks.add(ProtocolE.class);
        this.checks.add(ProtocolF.class);
        this.checks.add(ProtocolG.class);
        this.checks.add(ProtocolH.class);

        this.checks.add(ScaffoldA.class);

        this.checks.add(SpeedA.class);
        this.checks.add(SpeedB.class);

        this.checks.add(TimerA.class);

        this.checks.add(VelocityA.class);
        this.checks.add(VelocityB.class);
        this.checks.add(VelocityC.class);
    }

    @SneakyThrows
    public List<Check> loadChecks(final PlayerData data) {
        final List<Check> checkList = new ArrayList<>();

        for (final Class<?> check : this.checks) {
            checkList.add((Check) check.getConstructor(PlayerData.class).newInstance(data));
        }

        return checkList;
    }
}