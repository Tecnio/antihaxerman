package me.tecnio.antihaxerman.checks.impl.combat.reach;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.Config;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CheckInfo(name = "Reach", type = "A")
public final class ReachA extends Check {
    public ReachA(PlayerData data) {
        super(data);
    }

    @Override
    public void onAttack(WrappedPacketInUseEntity packet) {
        if (packet.getEntity() instanceof Player)data.setLastAttackedPlayer((Player) packet.getEntity());

        if (packet.getEntity() instanceof Player
                && packet.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK
                && !packet.getEntity().hasMetadata("NPC")
                && data.getEntityTracker().tracker != null
                && data.getEntityTracker().tracker.size() > 0){

            final PlayerData attackedData = DataManager.INSTANCE.getUser(packet.getEntity().getUniqueId());

            final Location eyeLoc = data.getPlayer().getEyeLocation();

            final double dist = data.getEntityTracker().getPredictedLocation(attackedData.getPing()).stream().mapToDouble(vector -> vector.clone().setY(0).distance(eyeLoc.toVector().clone().setY(0)) - 0.4).min().orElse(3);
            final double maxDist = data.getPlayer().getGameMode() == GameMode.CREATIVE ? (Config.MAX_REACH + 3.0) : Config.MAX_REACH;

            if (dist > maxDist){
                if (++preVL > 2)flag(data, "hit farther than possible! dist: " + dist);
            }else preVL = Math.max(0, preVL - 1);
        }
    }
}