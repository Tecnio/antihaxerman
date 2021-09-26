

package me.tecnio.antihaxerman.util;

import me.tecnio.antihaxerman.util.type.VpnInfo;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@UtilityClass
public class PlayerUtil {

    public ClientVersion getClientVersion(final Player player) {
        return PacketEvents.get().getPlayerUtils().getClientVersion(player);
    }

    public int getPing(final Player player) {
        return PacketEvents.get().getPlayerUtils().getPing(player.getUniqueId());
    }

    public VpnInfo isUsingVPN(final Player player) {
        try {
            final URL url = new URL("http://check.getipintel.net/check.php?ip=" + player.getAddress().getAddress().getHostAddress() + "&contact=amongus@gmail.com&oflags=c");
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.connect();
            if (!(connection.getResponseCode() == HttpURLConnection.HTTP_OK)) {
                Bukkit.getLogger().warning("[AHM] Vpn checker does not work! error code: " + connection.getResponseCode());
            }
            final String[] code = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine().split(",");
            connection.disconnect();
            if(code[0].equals("0")) {
                return new VpnInfo("N/A", false);
            } else if(code[0].equals("1")) {
                return new VpnInfo(new Locale("", code[1]).getDisplayCountry(), true);
            } else {
                return new VpnInfo("N/A", false);
            }
        }
        catch (Exception e) {
            Bukkit.broadcastMessage("exception");
            e.printStackTrace();
            return new VpnInfo("N/A", false);
        }

    }

    public EntityPlayer getEntityPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public int getDepthStriderLevel(final Player player) {
        if (player.getInventory().getBoots() != null && !ServerUtil.isLowerThan1_8()) {
            return player.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
        }
        return 0;
    }

    public float getBaseSpeed(final Player player, final float base) {
        return base + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public double getBaseSpeed(final Player player) {
        return 0.3615 + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public double getBaseGroundSpeed(final Player player) {
        return 0.289 + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    /**
     * Bukkit's getNearbyEntities method looks for all entities in all chunks
     * This is a lighter method and can also be used Asynchronously since we won't load any chunks
     *
     * @param location The location to scan for nearby entities
     * @param radius   The radius to expand
     * @return The entities within that radius
     * @author Nik
     */
    public List<Entity> getEntitiesWithinRadius(final Location location, final double radius) {
        final double expander = 16.0D;

        final double x = location.getX();
        final double z = location.getZ();

        final int minX = (int) Math.floor((x - radius) / expander);
        final int maxX = (int) Math.floor((x + radius) / expander);

        final int minZ = (int) Math.floor((z - radius) / expander);
        final int maxZ = (int) Math.floor((z + radius) / expander);

        final World world = location.getWorld();

        final List<Entity> entities = new LinkedList<>();

        try {
            for (int xVal = minX; xVal <= maxX; xVal++) {

                for (int zVal = minZ; zVal <= maxZ; zVal++) {

                    if (!world.isChunkLoaded(xVal, zVal)) continue;
                    if(world.getChunkAt(xVal, zVal) == null) {
                        continue;
                    }
                    for (final Entity entity : world.getChunkAt(xVal, zVal).getEntities()) {

                        if (entity == null) continue;


                        if (entity.getLocation().distanceSquared(location) > radius * radius) continue;

                        entities.add(entity);
                    }
                }
            }
        } catch(NoSuchElementException ignored) {

        }

        return entities;
    }

    public int getPotionLevel(final Player player, final PotionEffectType effect) {
        final int effectId = effect.getId();

        if (!player.hasPotionEffect(effect)) return 0;

        return player.getActivePotionEffects().stream().filter(potionEffect -> potionEffect.getType().getId() == effectId).map(PotionEffect::getAmplifier).findAny().orElse(0) + 1;
    }

}
