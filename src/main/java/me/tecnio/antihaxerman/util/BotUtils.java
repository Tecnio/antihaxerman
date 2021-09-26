package me.tecnio.antihaxerman.util;

import com.mojang.authlib.GameProfile;
import me.tecnio.antihaxerman.data.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@UtilityClass
public class BotUtils {

    public void removeBotEntity(PlayerData user) {
        if (user.getBotProcessor().isHasBot()) {
            user.getBotProcessor().setHasBot(false);
            user.getBotProcessor().setEntityHitTime(0);

            user.getEntityHelper().entityPlayer.setPosition(user.getPlayer().getLocation().getX() * -9999, user.getPlayer().getLocation().getY() * -9999, user.getPlayer().getLocation().getZ() * -9999);
            sendPacket(user, new PacketPlayOutEntityTeleport(user.getEntityHelper().entityPlayer), user.getBotProcessor().getForcedUser());

            user.getEntityHelper().entityPlayer = null;
        }
    }


    public void removeZombie(PlayerData user) {
        if (user.getBotProcessor().hasRaycastBot) {
            user.getBotProcessor().rayCastFailHitTimes = 0;
            user.getBotProcessor().hasRaycastBot = false;

            user.getEntityHelper().entityZombie.setPosition(user.getPlayer().getLocation().getX() *- 9999, user.getPlayer().getLocation().getY() *- 9999, user.getPlayer().getLocation().getZ() *- 9999);
            BotUtils.sendPacket(user, new PacketPlayOutEntityTeleport(user.getEntityHelper().entityZombie));

            user.getEntityHelper().entityPlayer2.setPosition(user.getPlayer().getLocation().getX() *- 9999, user.getPlayer().getLocation().getY() *- 9999, user.getPlayer().getLocation().getZ() *- 9999);
            BotUtils.sendPacket(user, new PacketPlayOutEntityTeleport(user.getEntityHelper().entityPlayer2));

            user.getEntityHelper().entityPlayer2 = null;
            user.getEntityHelper().entityZombie = null;
        }
    }

    public void sendPacket(PlayerData user, Packet packet) {
        PacketEvents.get().getPlayerUtils().sendNMSPacket(user.getPlayer(), packet);
    }

    public void sendPacket(PlayerData user, net.minecraft.server.v1_8_R3.Packet packet, PlayerData forcedUser) {
        PacketEvents.get().getPlayerUtils().sendNMSPacket(user.getPlayer(), packet);

        if (forcedUser != null) {
            PacketEvents.get().getPlayerUtils().sendNMSPacket(forcedUser.getPlayer(), packet);
        }
    }

    private Player getRandomPlayer(PlayerData user) {
        Player randomPlayer;
        if (Bukkit.getServer().getOnlinePlayers().size() > 1) {
            List<Player> onlinePlayers = new ArrayList<>();
            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                if (!online.getUniqueId().toString().equalsIgnoreCase(user.getPlayer().getUniqueId().toString()))
                    onlinePlayers.add(online);
            }
            randomPlayer = onlinePlayers.get(new Random().nextInt(onlinePlayers.size()));
        } else {
            randomPlayer = user.getPlayer();
        }
        return randomPlayer;
    }

    public Location getBehind(Player player, double multi) {
        Location location;
        location = player.getLocation().add(player.getEyeLocation().getDirection().multiply(multi));
        BlockFace facing = getCardinalFace(player);
        if (facing == BlockFace.WEST
                || facing == BlockFace.EAST) {
        }
        return location;
    }


    private String getCardinalDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 180) % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        return getDirection(rot);
    }

    private String getDirection(double rot) {
        if (0 <= rot && rot < 22.5) {
            return "North";
        } else if (22.5 <= rot && rot < 67.5) {
            return "Northeast";
        } else if (67.5 <= rot && rot < 112.5) {
            return "East";
        } else if (112.5 <= rot && rot < 157.5) {
            return "Southeast";
        } else if (157.5 <= rot && rot < 202.5) {
            return "South";
        } else if (202.5 <= rot && rot < 247.5) {
            return "Southwest";
        } else if (247.5 <= rot && rot < 292.5) {
            return "West";
        } else if (292.5 <= rot && rot < 0) {
            return "Northwest";
        } else if (310.5 <= rot && rot < 360) {
            return "North";
        } else {
            return "North";
        }
    }
    private BlockFace getCardinalFace(Player player) {
        String direction = getCardinalDirection(player);
        if (direction.equalsIgnoreCase("North"))
            return BlockFace.NORTH;
        if (direction.equalsIgnoreCase("Northeast"))
            return BlockFace.NORTH_EAST;
        if (direction.equalsIgnoreCase("East"))
            return BlockFace.EAST;
        if (direction.equalsIgnoreCase("Southeast"))
            return BlockFace.SOUTH_EAST;
        if (direction.equalsIgnoreCase("South"))
            return BlockFace.SOUTH;
        if (direction.equalsIgnoreCase("Southwest"))
            return BlockFace.SOUTH_WEST;
        if (direction.equalsIgnoreCase("West"))
            return BlockFace.WEST;
        if (direction.equalsIgnoreCase("Northwest"))
            return BlockFace.NORTH_WEST;
        return null;
    }


    public void spawnBotEntity(PlayerData user) {
        spawnBotEntity(user, null, BotTypes.NORMAL);
    }

    public void spawnBotEntity(PlayerData user, BotTypes botType) {
        spawnBotEntity(user, null, botType);
    }

    public void spawnBotEntity(PlayerData user, PlayerData forcedFrom, BotTypes botType) {
        if (!user.getBotProcessor().isHasBot() && user.getCombatProcessor().getTarget() != null) {
            user.getBotProcessor().setEntityAFollowDistance(-10.00);
            user.getBotProcessor().setEntityAReportedFlags(0);
            user.getBotProcessor().setEntityAMovementOffset(0.0f);
            user.getBotProcessor().setEntityAStartYaw(user.getRotationProcessor().getYaw());
            user.getBotProcessor().setHasBot(true);
            user.getBotProcessor().setEntityHitTime(0);

            user.getBotProcessor().setBotType(botType);

            if (forcedFrom != null) user.getBotProcessor().setForcedUser(forcedFrom);


            Player randomPlayer = getRandomPlayer(user);

            UUID uuid = randomPlayer.getUniqueId();
            String name = randomPlayer.getName();

            MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer worldServer = ((CraftWorld) user.getPlayer().getWorld()).getHandle();
            EntityPlayer entityPlayer = new EntityPlayer(minecraftServer, worldServer, new GameProfile(UUID.fromString(String.valueOf(uuid)), name), new PlayerInteractManager(worldServer));
            entityPlayer.onGround = true;
            entityPlayer.playerInteractManager.b(WorldSettings.EnumGamemode.CREATIVE);
            entityPlayer.setInvisible(false);
            entityPlayer.setHealth((float) MathUtil.getRandomDouble(MathUtil.getRandomDouble(1.20, 5.32), 20.0));
            entityPlayer.ping = ((CraftPlayer) randomPlayer).getHandle().ping;
            user.getBotProcessor().setBotID(entityPlayer.getId());

            user.getBotProcessor().lastEntitySpawn = System.currentTimeMillis();

            user.getEntityHelper().entityPlayer = entityPlayer;

            sendPacket(user, new PacketPlayOutNamedEntitySpawn(user.getEntityHelper().entityPlayer), forcedFrom);

            entityPlayer.setLocation(user.getPlayer().getLocation().getX(), user.getPlayer().getLocation().getY() + 0.42f, user.getPlayer().getLocation().getZ(), user.getPlayer().getLocation().getYaw(), (float) MathUtil.getRandomDouble(-90.0f, 90.0f));
            sendPacket(user, new PacketPlayOutEntityTeleport(entityPlayer), forcedFrom);

            sendPacket(user, new PacketPlayOutUpdateAttributes(), forcedFrom);

            if (randomPlayer.getItemInHand() != null) sendPacket(user, new PacketPlayOutEntityEquipment(entityPlayer.getId(), 0, CraftItemStack.asNMSCopy(randomPlayer.getItemInHand())), forcedFrom);
            sendPacket(user, new PacketPlayOutEntityEquipment(entityPlayer.getId(), 1, CraftItemStack.asNMSCopy(randomPlayer.getInventory().getBoots())), forcedFrom);
            sendPacket(user, new PacketPlayOutEntityEquipment(entityPlayer.getId(), 2, CraftItemStack.asNMSCopy(randomPlayer.getInventory().getLeggings())), forcedFrom);
            sendPacket(user, new PacketPlayOutEntityEquipment(entityPlayer.getId(), 3, CraftItemStack.asNMSCopy(randomPlayer.getInventory().getChestplate())), forcedFrom);
            sendPacket(user, new PacketPlayOutEntityEquipment(entityPlayer.getId(), 4, CraftItemStack.asNMSCopy(randomPlayer.getInventory().getHelmet())), forcedFrom);
            sendPacket(user, new PacketPlayOutUpdateAttributes(), forcedFrom);
        }
    }

}
