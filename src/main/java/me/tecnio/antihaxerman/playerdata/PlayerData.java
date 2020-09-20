package me.tecnio.antihaxerman.playerdata;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packet.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import lombok.Getter;
import lombok.Setter;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckManager;
import me.tecnio.antihaxerman.utils.LogUtils;
import me.tecnio.antihaxerman.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public final class PlayerData {

    private final Player player;

    private Location location, lastLocation, lastOnGroundLocation, lastLegitLocation;
    private Vector lastVel, direction;
    private double deltaXZ, deltaY, lastDeltaXZ, lastDeltaY;
    private float deltaYaw, deltaPitch, lastDeltaPitch, lastDeltaYaw, yaw, pitch;
    private short velocityID;
    private int ticks, airTicks, velTick, maxVelTicks, velocityTicks, iceTicks, legitTick, slimeTicks, liquidTicks, underBlockTicks, sprintingTicks, teleportTicks, groundTicks, totalFlags, cps;
    private long lastSetBack = System.nanoTime() / 1000000, lastShoot;
    private boolean isSprinting, isSneaking, onGround, serverOnGround, alerts, verifyingVelocity, digging;
    private List<Check> checks;

    private Player lastAttackedPlayer;
    public EntityTracker entityTracker = new EntityTracker();
    private ExecutorService executorService;
    private LogUtils.TextFile logFile;

    public PlayerData(UUID uuid){
        this.player = Bukkit.getPlayer(uuid);
        this.checks = CheckManager.loadChecks(this);
        executorService = Executors.newSingleThreadExecutor();
        logFile = new LogUtils.TextFile("" + uuid, "\\\\logs");
        Bukkit.getScheduler().runTaskTimerAsynchronously(AntiHaxerman.getInstance(), () -> {
            if(lastAttackedPlayer != null) {
                entityTracker.addLocation(lastAttackedPlayer.getLocation());
            }
        }, 0, 1);
    }

    public boolean isTakingVelocity() { return velocityTicks() < maxVelTicks; }

    public int getPing() { return PacketEvents.getAPI().getPlayerUtils().getPing(getPlayer()); }

    public boolean isOnClimbableBlock() { return PlayerUtils.isOnClimbable(this); }

    public boolean isInLiquid() { return PlayerUtils.inLiquid(this); }

    public boolean isInWeb() { return PlayerUtils.isInWeb(this); }

    public boolean isUnderBlock() { return PlayerUtils.blockNearHead(this); }

    public boolean isNearWall() { return PlayerUtils.nearWall(this); }

    public int velocityTicks() { return Math.abs(ticks - velTick); }

    public int teleportTicks() { return Math.abs(ticks - teleportTicks); }

    public int iceTicks() { return Math.abs(ticks - iceTicks); }

    public int slimeTicks() { return Math.abs(ticks - slimeTicks); }

    public int underBlockTicks() { return Math.abs(ticks - underBlockTicks); }

    public int liquidTicks() { return Math.abs(ticks - liquidTicks); }


    /*
    PacketShit
     */

    public void inbound(PacketReceiveEvent event){
        executorService.execute(() -> checks.forEach(check -> check.onPacketReceive(event)));
        if (event.getPacketId() == PacketType.Client.USE_ENTITY)onAttack(new WrappedPacketInUseEntity(event.getNMSPacket()));
        if (event.getPacketId() == PacketType.Client.POSITION || event.getPacketId() == PacketType.Client.POSITION_LOOK || event.getPacketId() == PacketType.Client.LOOK)onMove();
    }

    public void outgoing(PacketSendEvent event){
        executorService.execute(() -> checks.forEach(check -> check.onPacketSend(event)));
    }

    public void onAttack(WrappedPacketInUseEntity packet){
        executorService.execute(() -> checks.forEach(check -> check.onAttack(packet)));
    }

    public void onMove(){
        executorService.execute(() -> checks.forEach(Check::onMove));
    }
}
