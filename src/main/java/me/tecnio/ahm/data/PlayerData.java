package me.tecnio.ahm.data;

import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.PacketAPI;
import lombok.Getter;
import lombok.Setter;
import me.tecnio.ahm.AHM;
import me.tecnio.ahm.alert.AlertManager;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.CheckManager;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.check.type.PositionCheck;
import me.tecnio.ahm.check.type.RotationCheck;
import me.tecnio.ahm.data.tracker.Tracker;
import me.tecnio.ahm.data.tracker.impl.*;
import me.tecnio.ahm.processor.impl.IncomingPacketProcessor;
import me.tecnio.ahm.processor.impl.OutgoingPacketProcessor;
import me.tecnio.ahm.update.PositionUpdate;
import me.tecnio.ahm.update.RotationUpdate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public final class PlayerData {

    private final Player player;
    private final UUID uuid;

    private final IncomingPacketProcessor incomingPacketProcessor;
    private final OutgoingPacketProcessor outgoingPacketProcessor;

    private final Tracker[] trackers;

    private final PositionTracker positionTracker;
    private final RotationTracker rotationTracker;
    private final ActionTracker actionTracker;
    private final EntityTracker entityTracker;
    private final EmulationTracker emulationTracker;
    private final ConnectionTracker connectionTracker;
    private final AttributeTracker attributeTracker;
    private final VelocityTracker velocityTracker;
    private final GhostBlockTracker ghostBlockTracker;

    private final ExemptTracker exemptTracker;

    private final List<Check> checks;

    private final List<PacketCheck> packetChecks;
    private final List<PositionCheck> positionChecks;
    private final List<RotationCheck> rotationChecks;

    private PositionUpdate positionUpdate;
    private RotationUpdate rotationUpdate;

    private int ticks;

    public PlayerData(final Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();

        this.incomingPacketProcessor = new IncomingPacketProcessor(this);
        this.outgoingPacketProcessor = new OutgoingPacketProcessor(this);

        this.trackers = new Tracker[]{
                this.positionTracker = new PositionTracker(this),
                this.rotationTracker = new RotationTracker(this),
                this.actionTracker = new ActionTracker(this),
                this.connectionTracker = new ConnectionTracker(this),
                this.attributeTracker = new AttributeTracker(this),
                this.velocityTracker = new VelocityTracker(this),

                this.ghostBlockTracker = new GhostBlockTracker(this),
                this.entityTracker = new EntityTracker(this),
                this.emulationTracker = new EmulationTracker(this),
        };
        this.exemptTracker = new ExemptTracker(this);

        this.checks = AHM.get(CheckManager.class).loadChecks(this);

        this.packetChecks = this.checks.stream().filter(check -> check instanceof PacketCheck)
                .map(check -> ((PacketCheck) check)).collect(Collectors.toList());
        this.positionChecks = this.checks.stream().filter(check -> check instanceof PositionCheck)
                .map(check -> ((PositionCheck) check)).collect(Collectors.toList());
        this.rotationChecks = this.checks.stream().filter(check -> check instanceof RotationCheck)
                .map(check -> ((RotationCheck) check)).collect(Collectors.toList());

        if (this.player.hasPermission("ahm.alerts")) AHM.get(AlertManager.class).toggleAlerts(this);
    }

    public void terminate() {
        AHM.get(AlertManager.class).getPlayers().remove(this);
    }

    public void updateTicks() {
        ++this.ticks;
    }

    public void haram() {
        this.haram("Timed out");
    }

    public void haram(final String reason) {
        Bukkit.getScheduler().runTask(AHM.get().getPlugin(), () -> this.player.kickPlayer(reason));
    }

    public void send(final GPacket packet) {
        try {
            PacketAPI.sendPacket(this.player, packet);
        } catch (final Throwable ignored) {
            // vodka coding LLC I feel funny
        }
    }
}
