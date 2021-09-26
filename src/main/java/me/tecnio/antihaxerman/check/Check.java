package me.tecnio.antihaxerman.check;

import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.api.APIManager;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.manager.PunishmentManager;
import me.tecnio.antihaxerman.packet.Packet;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Objects;

@Getter
public abstract class Check {

    protected final PlayerData data;

    @Getter@Setter
    private String fullName;

    @Getter@Setter
    private int vl;
    @Getter@Setter
    private CheckType checkType;
    @Setter@Getter
    private int maxVl;
    @Getter
    private double buffer;
    @Setter
    private ArrayList<String> punishCommands;
    @Getter@Setter
    public int custom = 0;
    @Getter
    public boolean banning;

    @Getter@Setter
    public boolean enabled = true;

    @Setter
    private boolean debug;

    public Check(final PlayerData data) {
        this.data = data;

        final String packageName = this.getClass().getPackage().getName();

        fullName = this.getClass().getSimpleName();
        if (packageName.contains("combat")) {
            checkType = CheckType.COMBAT;
        } else if (packageName.contains("movement")) {
            checkType = CheckType.MOVEMENT;
        } else if (packageName.contains("player")) {
            checkType = CheckType.PLAYER;
        }
    }

    public abstract void handle(final Packet packet);

    public final void fail(final Object info) {
        if(!Config.TESTMODE && data.isBanning()) {
            return;
        }
        if (!data.isExempt() && enabled) {
            APIManager.callFlagEvent(this);

            if(Config.MAX_VIOLATIONS.get(getClass().getSimpleName()) != null) {
                this.setMaxVl(Config.MAX_VIOLATIONS.get(getClass().getSimpleName()));
            }
            if(AntiHaxerman.INSTANCE.getTickManager().getA() > 102) {
                AlertManager.handleAlertLag(this, data, Objects.toString(info));
            }
            else {
                ++vl;
                data.getMapchecks().put(this, vl);
                data.setTotalViolations(data.getTotalViolations() + 1);
                switch (checkType) {
                    case COMBAT:
                        data.setCombatViolations(data.getCombatViolations() + 1);
                        data.setBotViolations(data.getBotViolations() + 1);
                        break;
                    case MOVEMENT:
                        data.setMovementViolations(data.getMovementViolations() + 1);
                        break;
                    case PLAYER:
                        data.setPlayerViolations(data.getPlayerViolations() + 1);
                        break;
                }
                AlertManager.handleAlert(this, data, Objects.toString(info));
                if(this.getVl() == 3 && !PlayerDataManager.getInstance().suspectedPlayers.contains(data.getPlayer())) {
                    PlayerDataManager.getInstance().suspectedPlayers.add(data.getPlayer());
                }
                if(this.getVl() >= this.getMaxVl()) {
                    data.setBanning(true);
                    bannofail();
                }
            }
        }
    }

    public final void fail() {
        fail("No information.");
    }

    public final void ban() {
        if (!data.isExempt()) {
            fail();
            PunishmentManager.punish(this, data);
        }
    }

    public final void bannofail() {
        if (!data.isExempt()) {
            PunishmentManager.punish(this, data);
        }
    }

    public final void kick(final String reason) {
        if (!data.isExempt()) {
            fail();
            Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> data.getPlayer().kickPlayer(reason));
        }
    }

    protected final boolean isExempt(final ExemptType exemptType) {
        return data.getExemptProcessor().isExempt(exemptType);
    }

    protected final boolean isExempt(final ExemptType... exemptTypes) {
        return data.getExemptProcessor().isExempt(exemptTypes);
    }

    public final long now() {
        return System.currentTimeMillis();
    }

    public final int ticks() { return AntiHaxerman.INSTANCE.getTickManager().getTicks(); }

    public final double increaseBuffer() {
        return buffer = Math.min(10000, buffer + 1);
    }

    public final double increaseBufferBy(final double amount) {
        return buffer = Math.min(10000, buffer + amount);
    }

    public final double decreaseBuffer() {
        return buffer = Math.max(0, buffer - 1);
    }

    public final double decreaseBufferBy(final double amount) {
        return buffer = Math.max(0, buffer - amount);
    }

    public final void resetBuffer() {
        buffer = 0;
    }

    public final void setBuffer(final double amount) {
        buffer = amount;
    }

    public final void multiplyBuffer(final double multiplier) {
        buffer *= multiplier;
    }

    public final int hitTicks() {
        return data.getCombatProcessor().getHitTicks();
    }

    public final boolean digging() {
        return data.getActionProcessor().isDigging();
    }

    public final CheckInfo getCheckInfo() {
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            return this.getClass().getAnnotation(CheckInfo.class);
        } else {
            return null;
        }
    }


    public boolean isBridging() {
        if(isExempt(ExemptType.JOINED)) {
            return false;
        }
        return data.getPlayer().getLocation().clone().subtract(0, 2, 0).getBlock().getType() == Material.AIR && data.getPlayer().getLocation().clone().subtract(0, 1, 0).getBlock().getType().isSolid();
    }

    public final void debug(final Object object) {
        if (debug) {
            data.getPlayer().sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + "AHM-Debug" + ChatColor.GRAY + "] " + ChatColor.GRAY + object);
        }
    }

    public final void broadcast(final Object object) {
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + ChatColor.RED + "AHM-Debug" + ChatColor.GRAY + "] " + ChatColor.GRAY + object);
    }

    public enum CheckType {
        COMBAT, MOVEMENT, PLAYER;
    }
}
