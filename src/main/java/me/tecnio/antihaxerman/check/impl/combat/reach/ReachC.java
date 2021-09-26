package me.tecnio.antihaxerman.check.impl.combat.reach;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.manager.PlayerDataManager;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.PlayerUtil;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Reach", type = "C", description = "Checks if player is attacking from a slight different distance that's not possible.", experimental = true)
public class ReachC extends Check {

    public ReachC(PlayerData data) {
        super(data);
    }

    public float lastCVerbose;

    @Override
    public void handle(Packet packet) {
        if (packet.isUseEntity() ) {
            WrappedPacketInUseEntity wrapped = new WrappedPacketInUseEntity(packet.getRawPacket());
            if (!wrapped.getAction().equals(WrappedPacketInUseEntity.EntityUseAction.ATTACK) || !(wrapped.getEntity() instanceof Player))
                return;
            Player damager = data.getPlayer();
            Player victim = (Player) wrapped.getEntity();
            PlayerData dataDamager = PlayerDataManager.getInstance().getPlayerData(damager);
            PlayerData dataVictim = PlayerDataManager.getInstance().getPlayerData(victim);
            if (dataVictim == null) return;
            double reach = getReach(damager, victim);
            double yDiff = getYDiff(damager, victim);
            double yawDiff = getYawDiff(damager, victim);
            double maxReach = 3.0;
            if (yawDiff > 100) maxReach += yawDiff * .01;
            if (dataDamager.getActionProcessor().isSprinting() || PlayerUtil.getPotionLevel(damager, PotionEffectType.SPEED) > 0)
                maxReach += .12;
            maxReach += data.getPositionProcessor().getDeltaXZ() * .2;
            maxReach += yDiff * .42;
            maxReach += ((dataDamager.getConnectionProcessor().getKeepAlivePing() + dataDamager.getConnectionProcessor().getKeepAlivePing()) / 2) * .001;
            if (reach > maxReach) {
                increaseBuffer();
                lastCVerbose = System.currentTimeMillis();
            } else {
                if (lastCVerbose > 0) decreaseBuffer();
            }
            if (getBuffer() > 4) {
                resetBuffer();
                lastCVerbose = System.currentTimeMillis();
            }
        }

    }

    public double getReach(Player damager, Player victim) {
        return Math.hypot(damager.getLocation().getX() - victim.getLocation().getX(), damager.getLocation().getZ() - victim.getLocation().getZ()) - .4;
    }

    public double getYawDiff(Player damager, Player victim) {
        return Math.abs(180 - Math.abs(damager.getLocation().getYaw() - victim.getLocation().getYaw()));
    }

    public double getYDiff(Player damager, Player victim) {
        return Math.abs(damager.getLocation().getY() - victim.getLocation().getY());
    }


}
