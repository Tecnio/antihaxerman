package me.tecnio.antihaxerman.utils;

import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PlayerUtils {

    //Credits to Jonhan
    public static boolean onGround(PlayerData player) {
        Location location = player.getPlayer().getLocation();
        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (!location.clone().add(x, -0.05, z).getBlock().getType().equals(Material.AIR) && !location.clone().add(x, -0.05, z).getBlock().isLiquid())  return true;
                if (!location.clone().add(x, -0.5001, z).getBlock().getType().equals(Material.AIR) && !location.clone().add(x, -0.5001, z).getBlock().isLiquid())return true;
                if (isOnLilyOrCarpet(player))return true;
            }
        }
        return false;
    }

    public static boolean inLiquid(PlayerData data) {
        Player player = data.getPlayer();

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (player.getLocation().clone().add(z, 0, x).getBlock().isLiquid()) { return true; }
                if (player.getLocation().clone().add(z, player.getEyeLocation().getY(), x).getBlock().isLiquid()) { return true; }
            }
        }
        return false;
    }

    public static boolean blockNearHead(PlayerData data) {
        Player player = data.getPlayer();

        double expand = 0.5;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (player.getLocation().clone().add(z, 2.01, x).getBlock().getType() != Material.AIR) {
                    return true;
                }
                if (player.getLocation().clone().add(z, 1.5001, x).getBlock().getType() != Material.AIR) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnIce(PlayerData data) {
        Player p = data.getPlayer();
        if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE")
                || p.getLocation().clone().add(0, -0.5, 0).getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE")) {
            return true;
        }
        return false;
    }

    public static boolean isOnSlime(PlayerData data) {
        Player p = data.getPlayer();
        if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("SLIME")) {
            return true;
        }
        return false;
    }
    public static boolean isOnSlime(Location location) {
        return location.getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("SLIME");
    }

    public static boolean isOnLilyOrCarpet(PlayerData data) {
        Location loc = data.getPlayer().getLocation();
        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (loc.clone().add(z, 0, x).getBlock().getType().toString().contains("LILY")
                        || loc.clone().add(z, -0.001, x).getBlock().getType().toString().contains("CARPET")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOnWeirdBlock(PlayerData data) {
        Location loc = data.getPlayer().getLocation();
        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (loc.clone().add(z, 0, x).getBlock().getType().toString().contains("SLIME")
                        || loc.clone().add(z, -0.001, x).getBlock().getType().toString().contains("ICE")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isInWeb(PlayerData data) {
        Player player = data.getPlayer();

        double expand = 0.3;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (player.getLocation().clone().add(z, 0, x).getBlock().getType().toString().toLowerCase().contains("web")) { return true; }
                if (player.getLocation().clone().add(z, player.getEyeLocation().getY(), x).getBlock().getType().toString().toLowerCase().contains("web")) { return true; }
            }
        }
        return false;
    }

    public static boolean isOnClimbable(PlayerData data) {
        Player player = data.getPlayer();
        return player.getLocation().getBlock().getType() == Material.LADDER || player.getLocation().getBlock().getType() == Material.VINE || player.getLocation().clone().add(0, 1, 0).getBlock().getType() == Material.LADDER || player.getLocation().clone().add(0, 1, 0).getBlock().getType() == Material.VINE;
    }

    public static boolean nearWall(PlayerData data) {
        Player player = data.getPlayer();
        double expand = 0.6;
        for (double x = -expand; x <= expand; x += expand) {
            for (double z = -expand; z <= expand; z += expand) {
                if (player.getLocation().clone().add(z, 0.1, x).getBlock().getType() != Material.AIR) {
                    return true;
                }
                if (player.getEyeLocation().clone().add(z, 0, x).getBlock().getType() != Material.AIR) {
                    return true;
                }
            }
        }
        return false;
    }


    public static int getPotionEffectLevel(Player player, PotionEffectType pet) {
        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (!pe.getType().getName().equals(pet.getName())) continue;
            return pe.getAmplifier() + 1;
        }
        return 0;
    }

    public static float getBaseSpeed(Player player) {
        return 0.34f + (PlayerUtils.getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }

    public static float getBaseSpeed(Player player, float base) {
        return base + (PlayerUtils.getPotionEffectLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f) * 1.6f);
    }
}
