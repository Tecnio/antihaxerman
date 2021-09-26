package me.tecnio.antihaxerman.check.impl.movement.speed;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;
import me.tecnio.antihaxerman.util.type.BlockUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class SpeedF implements Listener {

    HashMap<Player, Integer> vls = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        vls.put(event.getPlayer(), -1);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        vls.remove(event.getPlayer());
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(!AntiHaxerman.INSTANCE.getPlugin().getConfig().getBoolean("checks.movement.speed.f.enabled")) {
            return;
        }
        PlayerData data = PlayerDataManager.getInstance().getPlayerData(e.getPlayer());
        if(data.getConnectionProcessor().getTransactionPing() == 0 || data.getConnectionProcessor().getKeepAlivePing() == 0) {
            return;
        }
        Location from = e.getFrom().clone();
        Location to = e.getTo().clone();
        Player p = e.getPlayer();
        Location l = p.getLocation();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        Location loc2 = new Location(p.getWorld(), x, y + 1, z);
        Location above = new Location(p.getWorld(), x, y + 2, z);
        if ((e.getTo().getX() == e.getFrom().getX()) && (e.getTo().getZ() == e.getFrom().getZ())
                && (e.getTo().getY() == e.getFrom().getY())
                || p.getNoDamageTicks() != 0
                || p.getVehicle() != null
                || e.isCancelled()
                || p.getGameMode().equals(GameMode.CREATIVE)
                || p.getAllowFlight()) return;
        double newmaxspeed = 0.7;
        double speed = MathUtil.offset(getHV(to.toVector()), getHV(from.toVector()));
        if (p.hasPotionEffect(PotionEffectType.SPEED)) {
            int level = getPotionEffectLevel(p, PotionEffectType.SPEED);
            if (level > 0) {
                newmaxspeed = (newmaxspeed * (((level * 20) * 0.011) + 1));
            }
        }
        if(data.getExemptProcessor().isExempt(ExemptType.COMBAT)) {
            newmaxspeed += 1;
        }
        if (data.getExemptProcessor().isExempt(ExemptType.NEARICE, ExemptType.ICE)) {
            newmaxspeed += .65;
        }
        for (Block b : BlockUtil.getNearbyBlocks(p.getLocation(), 3)) {
            if (b.getType().toString().contains("PISTON")) return;
        }
        if (p.getWalkSpeed() > .21) {
            newmaxspeed += p.getWalkSpeed() * 1.5;
        }
        if (speed > 0.4
                && speed >= newmaxspeed && p.getFallDistance() < 0.6
                && loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
                && loc2.getBlock().getType() == Material.AIR) {
            Check c = new Check(data) {
                @Override
                public void handle(Packet packet) {
                    return;
                }
            };
            c.setMaxVl(AntiHaxerman.INSTANCE.getPlugin().getConfig().getInt("checks.movement.speed.f.max-violations"));
            c.setPunishCommands((ArrayList<String>) AntiHaxerman.INSTANCE.getPlugin().getConfig().getStringList("checks.movement.speed.f.punish-commands"));
            c.setCheckType(Check.CheckType.MOVEMENT);
            c.setFullName("SpeedF");
            int newint = vls.get(e.getPlayer());
            newint++;
            vls.put(e.getPlayer(), newint);
            c.setVl(newint);
            c.setCustom(1);
            c.fail("SPEED: " + speed);
        }
    }

    private int getPotionEffectLevel(Player p, PotionEffectType pet) {
        for (PotionEffect pe : p.getActivePotionEffects()) {
            if (pe.getType().getName().equals(pet.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }

    private Vector getHV(Vector V) {
        V.setY(0);
        return V;
    }
}
