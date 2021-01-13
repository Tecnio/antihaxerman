/*
 *  Copyright (C) 2020 Tecnio
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package me.tecnio.antihaxerman.check;

import lombok.Getter;
import lombok.Setter;
import me.tecnio.antihaxerman.AntiHaxerman;
import me.tecnio.antihaxerman.api.APIManager;
import me.tecnio.antihaxerman.config.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.exempt.type.ExemptType;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.PunishmentManager;
import me.tecnio.antihaxerman.packet.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Objects;

@Getter
public abstract class Check {

    protected final PlayerData data;

    private int vl;
    private CheckType checkType;
    @Setter private int maxVl;
    private double buffer;
    @Setter private String punishCommand;

    public Check(final PlayerData data) {
        this.data = data;

        final String packageName = this.getClass().getPackage().getName();

        if (packageName.contains("combat")) {
            checkType = CheckType.COMBAT;
        } else if (packageName.contains("movement")) {
            checkType = CheckType.MOVEMENT;
        } else if (packageName.contains("player")) {
            checkType = CheckType.PLAYER;
        }
    }

    public abstract void handle(final Packet packet);

    public void fail(final Object info) {
        if (!data.getPlayer().hasPermission("antihaxerman.bypass") || Config.TESTMODE || !Config.BYPASS_OP) {
            if (!data.isExempt()) {
                APIManager.callFlagEvent(this);

                ++vl;
                data.setTotalViolations(data.getTotalViolations() + 1);

                switch (checkType) {
                    case COMBAT:
                        data.setCombatViolations(data.getCombatViolations() + 1);
                        break;
                    case MOVEMENT:
                        data.setMovementViolations(data.getMovementViolations() + 1);
                        break;
                    case PLAYER:
                        data.setPlayerViolations(data.getPlayerViolations() + 1);
                        break;
                }

                AlertManager.handleAlert(this, data, Objects.toString(info));
            }
        }
    }

    public void fail() {
        fail("No information.");
    }

    public void ban() {
        if (!data.getPlayer().hasPermission("antihaxerman.bypass") || Config.TESTMODE || !Config.BYPASS_OP) {
            if (!data.isExempt()) {
                fail();
                PunishmentManager.punish(this, data);
            }
        }
    }

    public void kick(final String reason) {
        if (!data.getPlayer().hasPermission("antihaxerman.bypass") || Config.TESTMODE || !Config.BYPASS_OP) {
            if (!data.isExempt()) {
                fail();
                Bukkit.getScheduler().runTask(AntiHaxerman.INSTANCE.getPlugin(), () -> data.getPlayer().kickPlayer(reason));
            }
        }
    }

    protected boolean isExempt(final ExemptType exemptType) {
        return data.getExemptProcessor().isExempt(exemptType);
    }

    protected boolean isExempt(final ExemptType... exemptTypes) {
        return data.getExemptProcessor().isExempt(exemptTypes);
    }

    public long now() {
        return System.currentTimeMillis();
    }

    public int ticks() { return AntiHaxerman.INSTANCE.getTickManager().getTicks(); }

    public double increaseBuffer() {
        return buffer = Math.min(10000, buffer + 1);
    }

    public double increaseBufferBy(final double amount) {
        return buffer = Math.min(10000, buffer + amount);
    }

    public double decreaseBuffer() {
        return buffer = Math.max(0, buffer - 1);
    }

    public double decreaseBufferBy(final double amount) {
        return buffer = Math.max(0, buffer - amount);
    }

    public void resetBuffer() {
        buffer = 0;
    }

    public void multiplyBuffer(final double multiplier) {
        buffer *= multiplier;
    }

    public int hitTicks() {
        return data.getCombatProcessor().getHitTicks();
    }

    public boolean digging() {
        return data.getActionProcessor().isDigging();
    }

    public CheckInfo getCheckInfo() {
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            return this.getClass().getAnnotation(CheckInfo.class);
        } else {
            System.err.println("CheckInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    public void debug(final Object object) {
        Bukkit.broadcastMessage(ChatColor.RED + "[AHM-Debug] " + ChatColor.GRAY + object);
    }

    public void debug(final Object... objects) {
        for (final Object object : objects) {
            Bukkit.broadcastMessage(ChatColor.RED + "[AHM-Debug] " + ChatColor.GRAY + object);
        }
    }

    enum CheckType {
        COMBAT, MOVEMENT, PLAYER;
    }
}
