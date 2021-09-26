package me.tecnio.antihaxerman.check.impl.player.breaker;

import me.tecnio.antihaxerman.check.Check;
import me.tecnio.antihaxerman.check.api.CheckInfo;
import me.tecnio.antihaxerman.data.PlayerData;
import me.tecnio.antihaxerman.packet.Packet;
import me.tecnio.antihaxerman.util.MathUtil;
import me.tecnio.antihaxerman.util.type.AABB;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.utils.vector.Vector3i;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@CheckInfo(name = "Breaker", type = "A", description = "Checks if player is breaking a block while not looking at it")
public class BreakerA extends Check {

    public BreakerA(PlayerData data) {
        super(data);
    }

    public Vector getHeadPosition() {
        Vector add = new Vector(0, 0, 0);
        add.setY(data.getActionProcessor().isSneaking() ? 1.54 : 1.62);
        return data.getPlayer().getLocation().clone().add(add).toVector();
    }

    @Override
    public void handle(Packet packet) {
        if(packet.isBlockDig()) {
            WrappedPacketInBlockDig wrapped = new WrappedPacketInBlockDig(packet.getRawPacket());
            Vector3i bLoc = wrapped.getBlockPosition();
            Location bLocWrapped = new Location(data.getPlayer().getWorld(), bLoc.x, bLoc.y, bLoc.z);
            Vector pos = getHeadPosition();
            Vector dir = MathUtil.getDirection(data.getRotationProcessor().getYaw(), data.getRotationProcessor().getPitch());
            Vector extraDir = MathUtil.getDirection(data.getRotationProcessor().getYaw() + data.getRotationProcessor().getDeltaYaw(),  data.getRotationProcessor().getPitch() +  data.getRotationProcessor().getDeltaPitch());
            if(wrapped.getDigType() != WrappedPacketInBlockDig.PlayerDigType.STOP_DESTROY_BLOCK) {
                return;
            }
            Vector min = bLocWrapped.toVector();
            Vector max = bLocWrapped.toVector().add(new Vector(1, 1, 1));
            AABB targetAABB = new AABB(min, max);

            if(!targetAABB.betweenRays(pos, dir, extraDir) && !isRight(min, max)) {
                fail("Min: " + min + " Max: " + max);
            }
        }
    }

    public boolean isRight(Vector min, Vector max) {
        return (min.getX() - 1) == max.getX() && (min.getY() - 1) == max.getY() && (min.getZ() - 1) == max.getZ();
    }
}
