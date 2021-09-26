package me.tecnio.antihaxerman.data.processor;

import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.util.BotTypes;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class BotProcessor{

    public int botID, rayCastEntityID, entityAReportedFlags, botTicks, entityATotalAttacks, movedBotTicks, randomBotSwingTicks, randomBotDamageTicks, rayCastFailHitTimes;
    public boolean hasBot, moveBot, WaitingForBot, hasRaycastBot, hasHitRaycast;
    public BotTypes botType;
    public double EntityAFollowDistance, rayCastEntityRoation;
    public float EntityAMovementOffset, EntityAStartYaw, rayCastStartYaw;
    public long lastEntitySpawn, entityHitTime, lastEntityBotHit, lastRaycastSpawn, lastRaycastGood, raycastEntity2HitTimes;
    public PlayerData forcedUser;

}
