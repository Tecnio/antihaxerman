package me.tecnio.antihaxerman.checks.impl.combat.angle;

import io.github.retrooper.packetevents.packetwrappers.in.useentity.WrappedPacketInUseEntity;
import me.tecnio.antihaxerman.checks.Check;
import me.tecnio.antihaxerman.checks.CheckInfo;
import me.tecnio.antihaxerman.playerdata.DataManager;
import me.tecnio.antihaxerman.playerdata.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Angle", type = "A")
public final class AngleA extends Check {
    public AngleA(PlayerData data) {
        super(data);
    }

    @Override
    public void onAttack(WrappedPacketInUseEntity packet) {
        if (packet.getEntity() instanceof Player
                && packet.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK
                && !packet.getEntity().hasMetadata("NPC")
                && data.getEntityTracker().tracker != null
                && data.getEntityTracker().tracker.size() > 0) {
            final PlayerData attackedData = DataManager.INSTANCE.getUser(data.getLastAttackedPlayer().getUniqueId());

            final double angle =
                    data.getEntityTracker()
                    .getPredictedLocation(attackedData.getTransactionPing())
                    .stream()
                    .mapToDouble(vector -> {
                        final Vector dirToDestination = vector.clone().setY(0.0).subtract(data.getPlayer().getEyeLocation().toVector().setY(0.0));
                        final Vector playerDirection = data.getPlayer().getEyeLocation().getDirection().setY(0.0);

                        return dirToDestination.angle(playerDirection);
                    })
                    .min().orElse(0.0);

            if(angle > 0.5 && data.getPlayer().getLocation().toVector().setY(0.0).distance(data.getLastAttackedPlayer().getLocation().toVector().setY(0.0)) > 1.0) {
                if(++buffer > 2) {
                    flag(data, "angle = " + angle + ", rotation = " + data.getDeltaYaw());
                }
            } else buffer = 0;
        }
    }
}
