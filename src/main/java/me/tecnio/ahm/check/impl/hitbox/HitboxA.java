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
            // As we are brute forcing some possibilities, we just need to see if any of them intersected;
            boolean intersection = false;

            // These are the tracked positions of the target that was attacked;
            final double targetX = this.target.getPosX();
            final double targetY = this.target.getPosY();
            final double targetZ = this.target.getPosZ();

            // As we are going to ray-cast we need to create an appropriately sized bounding box for the target.
            AxisAlignedBB boundingBox = new AxisAlignedBB(
                    targetX - 0.4F, targetY - 0.1F, targetZ - 0.4F,
                    targetX + 0.4F, targetY + 1.9F, targetZ + 0.4F
            );

            // Because of something called 0.03 we sometimes get uncertainty in the position of the attacker.
            // To handle this we can just add a 0.03 block leniency on the targets bounding box.
            // Not a perfect solution but a valid one for sure.
            if (this.isExempt(ExemptType.RETARD)) boundingBox = boundingBox.expand(0.03, 0.03, 0.03);

            // On 1.8.9, the rotations of the player is a tick behind but very popular mods like OptiFine fix this problem,
            // and therefore we have to handle them as well.
            for (final boolean rotation : BOOLEANS) {
                // This is for the sneaking status of the player, because the latest sneaking position doesn't work
                // and I can't be asked to find the proper sneaking state of the player, we are brute forcing it.
                for (final boolean sneak : BOOLEANS) {
                    // These are the yaw and pitch of the player. For the explanation of this read the rotation iterations comment.
                    final float yaw = rotation ? data.getRotationTracker().getYaw() : data.getRotationTracker().getLastYaw();
                    final float pitch = rotation ? data.getRotationTracker().getPitch() : data.getRotationTracker().getLastPitch();

                    // This is where we do the actual ray-casting.
                    final MovingObjectPosition result = this.rayCast(yaw, pitch, sneak, boundingBox);

                    // Here, if we have an intersection we set the intersection variable to true, if else it stays false.
                    intersection |= result != null && result.hitVec != null;
                }
            }

            final Player target = (Player) data.getActionTracker().getTarget();

            // These are some scenarios the check wouldn't work and instead of properly handling it, we are returning
            // because who actually gives a fuck about these scenarios.
            final boolean exempt = data.getPlayer().getGameMode() == GameMode.CREATIVE
                    || target.isInsideVehicle() || target.isSleeping();

            if (!intersection && !exempt) {
                // This check does need some buffer as it's not flawless but the concept is there, ready to be expanded on.
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
                this.target = data.getEntityTracker().getTrackerMap().getOrDefault(wrapper.getEntityId(), null);
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

    // better not be using fastmath my guy
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
