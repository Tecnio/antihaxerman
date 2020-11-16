package me.tecnio.antihaxerman.check;

import io.github.retrooper.packetevents.event.impl.PacketReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketSendEvent;
import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import lombok.Getter;
import lombok.Setter;
import me.tecnio.antihaxerman.Config;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.AlertManager;
import me.tecnio.antihaxerman.manager.PunishmentManager;

@Getter @Setter
public abstract class Check {
    protected final PlayerData data;

    protected double buffer;
    private int vl;
    private boolean flagging;

    public boolean enabled;

    public Check(PlayerData data) {
        this.data = data;
        enabled = Config.ENABLED_CHECKS.get(this.getCheckInfo().name());
    }

    public CheckInfo getCheckInfo() {
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            return this.getClass().getAnnotation(CheckInfo.class);
        } else {
            System.err.println("CheckInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    protected void flag() {
        flag("No information.");
    }

    protected void flag(String information) {
        if (!flagging) {
            flagging = true;

            vl++;
            AlertManager.alertCheck(data, this, information);

            if (vl >= getCheckInfo().maxVL()) {
                PunishmentManager.punish(this, data);
            }

            flagging = false;
        }
    }


    public void onPacketReceive(final PacketReceiveEvent event) { }
    public void onPacketSend(final PacketSendEvent event) { }
    public void onFlying() {}
    public void onMove() {}
    public void onRotation() {}
    public void onAttack(final WrappedPacketInUseEntity wrapper) {}

    public double increaseBufferBy(final double num) {
        buffer = Math.min(Double.MAX_VALUE, buffer + num);
        return buffer;
    }

    public double increaseBuffer() {
        buffer = Math.min(Double.MAX_VALUE, buffer + 1);
        return buffer;
    }

    public double decreaseBuffer() {
        buffer = Math.max(0, buffer - 1);
        return buffer;
    }

    public double decreaseBufferBy(final double num) {
        buffer = Math.max(0, buffer - num);
        return buffer;
    }

    public void resetBuffer() {
        buffer = 0;
    }
}
