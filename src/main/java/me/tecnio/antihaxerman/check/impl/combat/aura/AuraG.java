package me.tecnio.antihaxerman.check.impl.combat.aura;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockIterator;

@CheckInfo(name = "Aura", type = "G", description = "Checks for line of sight.")
public class AuraG extends Check {

    public AuraG(PlayerData data) {
        super(data);
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isUseEntity()) {
            WrappedPacketInUseEntity wrapped = new WrappedPacketInUseEntity(packet.getRawPacket());
            if(wrapped.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                Entity ent = wrapped.getEntity();
                if(ent != null) {
                    if(!isInLineOfSight(ent) && increaseBuffer() > 3) {
                        fail();
                    }
                    else {
                        decreaseBufferBy(0.05);
                    }
                }
            }
        }
    }

    private boolean isInLineOfSight(Entity check) {
        final Location entityLocation = check.getLocation();
        final BlockIterator iterator = new BlockIterator(data.getPlayer().getEyeLocation(), 0.0, 7);

        while (iterator.hasNext()) {
            final Location current = iterator.next().getLocation();

            if (getLocationDifference(current, entityLocation, "X") < 2.25
                    && getLocationDifference(current, entityLocation, "Y") < 2.25
                    && getLocationDifference(current, entityLocation, "Z") < 2.25) {
                return true;
            }
        }

        // The entity has not been found in the player's line of sight.
        return false;
    }

    private double getLocationDifference(Location first, Location second, String axis) {
        double difference = 0.0;

        switch (axis) {
            case "X":
                difference = first.getX() - second.getX();
                break;
            case "Y":
                difference = first.getY() - second.getY();
                break;
            case "Z":
                difference = first.getZ() - second.getZ();
                break;
        }

        return Math.abs(difference);
    }
}
