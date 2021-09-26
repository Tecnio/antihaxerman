

package me.tecnio.antihaxerman.listener.bukkit;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.ColorUtil;
import me.tecnio.antihaxerman.util.LogUtil;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class BukkitEventManager implements Listener {

    private HashMap<UUID, Projectile> selfdmg = new HashMap<>();

    public static ArrayList<Player> wannadelet = new ArrayList<>();

    public static ArrayList<UUID> selfdmg2 = new ArrayList<>();

    public static ArrayList<UUID> dmg = new ArrayList<>();

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());
        if (data != null) {
            data.getActionProcessor().handleInteract(event);
        }
    }
    @EventHandler
    public void onSelfDamage(ProjectileLaunchEvent event) {
        if(event.getEntity().getType() == EntityType.ARROW || event.getEntity().getType() == EntityType.SNOWBALL) {
            if(event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player) {
                selfdmg.put(((Player) event.getEntity().getShooter()).getUniqueId(), event.getEntity());
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!dmg.contains(event.getEntity().getUniqueId())) {
                dmg.add(event.getEntity().getUniqueId());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        dmg.remove(event.getEntity().getUniqueId());
                    }
                }.runTaskLater(AntiHaxerman.INSTANCE.getPlugin(), 180);
            }
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        if(wannadelet.contains(event.getPlayer())) {
            event.setCancelled(true);
            if(event.getMessage().startsWith("cancel")) {
                wannadelet.remove(event.getPlayer());
                event.getPlayer().sendMessage(ChatColor.GREEN + "Cancelled the action.");
            }
            else if(event.getMessage().equals("YES")) {
                new File(AntiHaxerman.INSTANCE.getPlugin().getDataFolder() + File.separator + "logs").mkdir();
                for(File file : Objects.requireNonNull(new File(AntiHaxerman.INSTANCE.getPlugin().getDataFolder() + File.separator + "logs").listFiles())) {
                    if(!file.getName().startsWith("config")) {
                        LogUtil.TextFile filetext = new LogUtil.TextFile(file.getName(), file.getPath());
                        LogUtil.resetFile(filetext);
                    }
                }
                event.getPlayer().sendMessage(ColorUtil.translate("&aCompleted resetting the files!"));
                wannadelet.remove(event.getPlayer());
            }
            else {
                event.getPlayer().sendMessage(ChatColor.GREEN + "Please respond to the action started before, or cancel it by typing \"cancel\".");
            }
        }
    }

    @EventHandler
    public void onSelfDamage2(EntityDamageByEntityEvent event) {
        if(event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if(event.getEntity() instanceof Player) {
                if(selfdmg.get(event.getEntity().getUniqueId()) == event.getDamager()) {
                    selfdmg2.add(event.getEntity().getUniqueId());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                          selfdmg2.remove(event.getEntity().getUniqueId());
                        }
                    }.runTaskLater(AntiHaxerman.INSTANCE.getPlugin(), 5);
                }
            }
        }
    }
    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        final PlayerData data = PlayerDataManager.getInstance().getPlayerData(event.getPlayer());
        if (data != null) {
            data.getActionProcessor().handleBukkitPlace();
            AntiHaxerman.INSTANCE.getReceivingPacketProcessor().handle(data, new Packet(Packet.Direction.RECEIVE,
                    new NMSPacket(event), Byte.MAX_VALUE, System.currentTimeMillis()));
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if(event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player) {
            PlayerDataManager.getInstance().getPlayerData((Player) event.getEntity().getShooter()).setEnderpearlTime(System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        PlayerDataManager.getInstance().getPlayerData(event.getPlayer()).setRespawnTime(System.currentTimeMillis());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        PlayerDataManager.getInstance().getPlayerData(event.getPlayer()).getActionProcessor().handleDrop();
    }
}
