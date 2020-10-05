package me.tecnio.antihaxerman.playerdata;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.out.transaction.WrappedPacketOutTransaction;
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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@Setter
public final class PlayerData {

    private final Player player;

    private Location location, lastLocation, lastOnGroundLocation, lastLegitLocation;
    private Vector lastVelocity, direction;
    private double deltaXZ, deltaY, lastDeltaXZ, lastDeltaY;
    private float deltaYaw, deltaPitch, lastDeltaPitch, lastDeltaYaw, yaw, pitch;
    private short velocityID, transPingID;
    private int ticks, airTicks, velTick, maxVelTicks, velocityTicks, attackTicks, iceTicks, legitTick, slimeTicks, liquidTicks, underBlockTicks, sprintingTicks, teleportTicks, groundTicks, totalFlags, cps, transactionPing;
    private long lastSetBack = System.nanoTime() / 1000000, lastShoot, transPingSent;
    private boolean isSprinting, isSneaking, blocking, onGround, serverOnGround, alerts, verifyingVelocity, digging;
    private List<Check> checks;

    private Player lastAttackedPlayer;
    private EntityTracker entityTracker;

    private ExecutorService executorService;
    private LogUtils.TextFile logFile;

    private final Random random = new Random();

    public PlayerData(UUID uuid){
        this.player = Bukkit.getPlayer(uuid);
        this.checks = CheckManager.loadChecks(this);
        executorService = Executors.newSingleThreadExecutor();
        logFile = new LogUtils.TextFile("" + uuid, "\\\\logs");
        entityTracker = new EntityTracker();
        Bukkit.getScheduler().runTaskTimerAsynchronously(AntiHaxerman.getInstance(), () -> {
            if(lastAttackedPlayer != null) {
                entityTracker.addLocation(lastAttackedPlayer.getLocation());
            }
        }, 0, 1);

        transPingID = (short) random.nextInt(32767);
        PacketEvents.getAPI().getPlayerUtils().sendPacket(player, new WrappedPacketOutTransaction(0, transPingID, false) );
        transPingSent = System.currentTimeMillis();
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

    public int sprintingTicks() { return Math.abs(ticks - sprintingTicks); }

    public int attackTicks() { return Math.abs(ticks - attackTicks); }


    /*
    PacketShit
     */

    public void inbound(PacketReceiveEvent event){
        if (event.getPacketId() == PacketType.Client.USE_ENTITY)onAttack(new WrappedPacketInUseEntity(event.getNMSPacket()));
        if (event.getPacketId() == PacketType.Client.POSITION || event.getPacketId() == PacketType.Client.POSITION_LOOK || event.getPacketId() == PacketType.Client.LOOK)onMove();
        executorService.execute(() -> checks.forEach(check -> check.onPacketReceive(event)));
    }

    public void outgoing(PacketSendEvent event){
        executorService.execute(() -> checks.forEach(check -> check.onPacketSend(event)));
    }

    public void onAttack(WrappedPacketInUseEntity packet){
        if (packet.getEntity() instanceof Player) lastAttackedPlayer = (Player) packet.getEntity();
        executorService.execute(() -> checks.forEach(check -> check.onAttack(packet)));
    }

    public void onMove(){
        executorService.execute(() -> checks.forEach(Check::onMove));
    }
}
