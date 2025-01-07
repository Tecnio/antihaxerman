package me.tecnio.ahm.check.impl.hitbox;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientUseEntity;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;
import me.tecnio.ahm.check.Check;
import me.tecnio.ahm.check.api.annotations.CheckManifest;
import me.tecnio.ahm.check.type.PacketCheck;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.exempt.ExemptType;
import me.tecnio.ahm.util.mcp.AxisAlignedBB;
import me.tecnio.ahm.util.mcp.MathHelper;
import me.tecnio.ahm.util.mcp.MovingObjectPosition;
import me.tecnio.ahm.util.mcp.Vec3;
import me.tecnio.ahm.util.player.TrackerEntity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CheckManifest(name = "Hitbox", type = "A", description = "Detects any reach or hitbox modifications.")
public final class HitboxA extends Check implements PacketCheck {
    private static final boolean[] BOOLEANS = {true, false};

    private TrackerEntity target;

    public HitboxA(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof PacketPlayClientFlying && this.target != null) {
            boolean intersection = false;

            final double targetX = this.target.getPosX();
            final double targetY = this.target.getPosY();
            final double targetZ = this.target.getPosZ();

            AxisAlignedBB boundingBox = new AxisAlignedBB(
                    targetX - 0.4F, targetY - 0.1F, targetZ - 0.4F,
                    targetX + 0.4F, targetY + 1.9F, targetZ + 0.4F
            );

            if (this.isExempt(ExemptType.SLOW)) boundingBox = boundingBox.expand(0.03, 0.03, 0.03);

            for (final boolean rotation : BOOLEANS) {
                for (final boolean sneak : BOOLEANS) {
                    final float yaw = rotation
                            ? data.getRotationTracker().getYaw()
                            : data.getRotationTracker().getLastYaw();
                    final float pitch = data.getRotationTracker().getPitch();

                    final MovingObjectPosition result = this.rayCast(yaw, pitch, sneak, boundingBox);

                    intersection |= result != null && result.hitVec != null;
                }
            }

            final Player target = (Player) data.getActionTracker().getTarget();

            final boolean exempt = data.getPlayer().getGameMode() == GameMode.CREATIVE
                    || target.isInsideVehicle()
                    || target.isSleeping();

            if (!intersection && !exempt) {
                if (this.buffer.increase() > 2) {
                    this.fail();
                }
            } else {
                this.buffer.decreaseBy(0.01D);
            }

            this.target = null;
        }

        else if (packet instanceof PacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = ((GPacketPlayClientUseEntity) packet);

            if (wrapper.getType() == PlayerEnums.UseType.ATTACK) {
                this.target = data.getEntityTracker().getTrackerMap()
                        .get(wrapper.getEntityId());
            }
        }
    }

    private MovingObjectPosition rayCast(final float yaw, final float pitch, final boolean sneak, final AxisAlignedBB bb) {
        final Location position = data.getPositionTracker().getLastLocation();

        final double lastX = position.getX();
        final double lastY = position.getY();
        final double lastZ = position.getZ();

        final Vec3 vec3 = new Vec3(lastX, lastY + this.getEyeHeight(sneak), lastZ);
        final Vec3 vec31 = this.getVectorForRotation(pitch, yaw);
        final Vec3 vec32 = vec3.add(new Vec3(vec31.xCoord * 3.0D, vec31.yCoord * 3.0D, vec31.zCoord * 3.0D));

        return bb.calculateIntercept(vec3, vec32);
    }

    private Vec3 getVectorForRotation(final float pitch, final float yaw) {
        final float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        final float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        final float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        final float f3 = MathHelper.sin(-pitch * 0.017453292F);

        return new Vec3(f1 * f2, f3, f * f2);
    }

    public float getEyeHeight(final boolean sneak) {
        float f2 = 1.62F;

        if (data.getPlayer().isSleeping()) {
            f2 = 0.2F;
        }

        if (sneak) {
            f2 -= 0.08F;
        }

        return f2;
    }
}
