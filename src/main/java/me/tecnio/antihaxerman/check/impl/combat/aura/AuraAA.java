package me.tecnio.antihaxerman.check.impl.combat.aura;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.BotTypes;
import me.tecnio.antihaxerman.util.BotUtils;
import me.tecnio.antihaxerman.util.MathUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@CheckInfo(name = "Aura", type = "AA", description = "Checks if player is wearing china hat.                   (Its a bot check)")
public class AuraAA extends Check {

    public AuraAA(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if (packet.isFlying() && data.getEntityHelper().entityPlayer != null) {

            if ((System.currentTimeMillis() - data.getBotProcessor().lastEntityBotHit) > 500L) {
                if (data.getBotProcessor().getEntityHitTime() > 0) data.getBotProcessor().entityHitTime--;
            }

            if (data.getBotProcessor().getEntityHitTime() > 7 && (System.currentTimeMillis() - data.getBotProcessor().lastEntityBotHit) < 320L) {
                if (data.getBotProcessor().getForcedUser() == null) {
                    fail("HitTime: " + data.getBotProcessor().getEntityHitTime());
                } else {
                    data.getBotProcessor().entityAReportedFlags++;
                }
            }

            long seconds = (System.currentTimeMillis() - data.getBotProcessor().lastEntitySpawn) / 1000;
            debug(seconds);
            if (seconds > 10L) {
                if (data.getBotProcessor().getForcedUser() != null && data.getBotProcessor().getForcedUser().getPlayer().isOnline()) {
                    data.getBotProcessor().getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + "Bot report for " + ChatColor.RED + data.getPlayer().getName());
                    data.getBotProcessor().getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + "Sample taken: " + ChatColor.GREEN + seconds + "s");
                    data.getBotProcessor().getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + "Total Attacks: " + ChatColor.GREEN + data.getBotProcessor().entityATotalAttacks);
                    data.getBotProcessor().getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + "Total Valid Attacks (Flags): " + ChatColor.GREEN + data.getBotProcessor().entityAReportedFlags);
                    data.getBotProcessor().getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + "Prediction:");
                    data.getBotProcessor().getForcedUser().getPlayer().sendMessage(ChatColor.GRAY + " - " + (data.getBotProcessor().entityAReportedFlags > 3 ? ChatColor.RED + "Cheating" : (data.getBotProcessor().getEntityAReportedFlags() > 5 ? ChatColor.YELLOW + "Possibly Legit" : ChatColor.GREEN + "Legit")));
                }
                BotUtils.removeBotEntity(data);
                data.getBotProcessor().setForcedUser(null);
                data.getBotProcessor().entityATotalAttacks = 0;
                data.getBotProcessor().entityAReportedFlags = 0;
                data.getBotProcessor().setWaitingForBot(false);
                return;
            }

            Location playerloc = new Location(data.getPlayer().getLocation().getWorld(), data.getPositionProcessor().getX(), data.getPositionProcessor().getY(), data.getPositionProcessor().getZ());
            Location loc = BotUtils.getBehind(data.getPlayer(), (data.getBotProcessor().moveBot ? (!(playerloc.getPitch() < -21.00f) ? -0.10 : -2.90) : -2.90));

            if (data.getBotProcessor().getBotType() == BotTypes.WATCHDOG) loc = data.getPlayer().getLocation();

            boolean random = ThreadLocalRandom.current().nextBoolean();
            double offset = (random ? MathUtil.getRandomDouble(0.20, 0.55) : 0.0);
            boolean hit = ((System.currentTimeMillis() - data.getBotProcessor().lastEntityBotHit) < 122L);


            if (data.getBotProcessor().botTicks > 50) {
                if (!data.getBotProcessor().moveBot) {
                    data.getBotProcessor().moveBot = true;
                }
                data.getBotProcessor().botTicks = 0;
            }

            if (data.getBotProcessor().moveBot && data.getBotProcessor().movedBotTicks > 20) {
                data.getBotProcessor().movedBotTicks = 0;
                data.getBotProcessor().moveBot = false;
            }

            if (data.getBotProcessor().getBotType() == BotTypes.NORMAL) {
                Random r = new Random();
                int low = 0;
                int high = 20;
                int result = r.nextInt(high-low) + low;
                if(data.getRotationProcessor().getPitch() <= -80) {
                    data.getEntityHelper().entityPlayer.setLocation(loc.getX() + offset, loc.getY() - MathUtil.getRandomDouble(0.5f, 1f), loc.getZ() - offset, (float) (loc.getYaw() + MathUtil.getRandomDouble(0.10f, 0.50f)), (float) MathUtil.getRandomDouble(-90.0f, 90.0f));
                }
                else {
                    data.getEntityHelper().entityPlayer.setLocation(loc.getX() + offset, ((hit || data.getBotProcessor().moveBot) && !(playerloc.getPitch() < -6.00f) ? loc.getY() + 3.42 : loc.getY() + (random && result < 15 ? MathUtil.getRandomDouble(0.10, 0.99) : 0.0)), loc.getZ() - offset, (float) (loc.getYaw() + MathUtil.getRandomDouble(0.10f, 0.50f)), (float) MathUtil.getRandomDouble(-90.0f, 90.0f));
                }
            } else if (data.getBotProcessor().getBotType() == BotTypes.WATCHDOG) {
                double incressment = MathUtil.getRandomDouble(0.95, 1.40);
                data.getEntityHelper().entityPlayer.setLocation(loc.getX() + Math.sin(Math.toRadians(-(data.getBotProcessor().getEntityAStartYaw() + data.getBotProcessor().getEntityAMovementOffset()))) * incressment, loc.getY() + 1 + (ThreadLocalRandom.current().nextBoolean() ? (ThreadLocalRandom.current().nextBoolean() ? MathUtil.getRandomDouble(0.35f, 0.42f) : 0.42f) : 0.0f), loc.getZ() + Math.cos(Math.toRadians(-(data.getBotProcessor().getEntityAStartYaw() + data.getBotProcessor().getEntityAMovementOffset()))) * incressment, (float) (loc.getYaw() + MathUtil.getRandomDouble(0.10f, 0.50f)), (float) MathUtil.getRandomDouble(-90.0f, 90.0f));
            } else if (data.getBotProcessor().getBotType() == BotTypes.FOLLOW) {
                if (Math.abs(data.getBotProcessor().getEntityAFollowDistance()) > 1.20) {
                    data.getBotProcessor().setEntityAFollowDistance(data.getBotProcessor().getEntityAFollowDistance() + 0.05f);
                }

                double yaw = 0.0f, amount = -data.getBotProcessor().getEntityAFollowDistance();
                yaw = Math.toRadians(yaw);
                double dX = -Math.sin(yaw) * amount;
                double dZ = Math.cos(yaw) * amount;

                data.getEntityHelper().entityPlayer.setLocation(loc.getX() + dX, playerloc.getY(), loc.getZ() + dZ, playerloc.getYaw(), playerloc.getPitch());
            }

            BotUtils.sendPacket(data, new PacketPlayOutEntityTeleport(data.getEntityHelper().entityPlayer), data.getBotProcessor().getForcedUser());

            if (!data.getBotProcessor().moveBot) data.getBotProcessor().botTicks++;
            else data.getBotProcessor().movedBotTicks++;
            data.getBotProcessor().randomBotSwingTicks++;
            data.getBotProcessor().randomBotDamageTicks++;
            data.getBotProcessor().setEntityAMovementOffset(data.getBotProcessor().getEntityAMovementOffset() + 20.0f);
        }

        if (packet.isUseEntity()) {
            WrappedPacketInUseEntity wrappedInUseEntityPacket = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (wrappedInUseEntityPacket.getEntity() != null && wrappedInUseEntityPacket.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                if (data.getBotViolations() >= 3 && wrappedInUseEntityPacket.getEntity() instanceof Player) {
                    data.setBotViolations(0);
                    BotUtils.spawnBotEntity(data);
                }
            }

            if (data.getBotProcessor().hasBot && wrappedInUseEntityPacket.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK && wrappedInUseEntityPacket.getEntityId() == data.getBotProcessor().botID) {
                if (data.getBotProcessor().entityHitTime < 20) data.getBotProcessor().entityHitTime++;
                data.getBotProcessor().lastEntityBotHit = System.currentTimeMillis();
                data.getBotProcessor().entityATotalAttacks++;
            }
        }
    }

}
