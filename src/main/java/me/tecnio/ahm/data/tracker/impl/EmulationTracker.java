package me.tecnio.ahm.data.tracker.impl;

import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import lombok.Getter;
import me.tecnio.ahm.data.PlayerData;
import me.tecnio.ahm.data.tracker.Tracker;
import me.tecnio.ahm.util.math.OptiFineShitMath;
import me.tecnio.ahm.util.mcp.MathHelper;
import me.tecnio.ahm.util.player.PlayerUtil;
import me.tecnio.ahm.util.type.Motion;

@Getter
public final class EmulationTracker extends Tracker {

    private Motion motion;

    // Stores the smallest outcome
    private double distance = Double.MAX_VALUE;

    private boolean sprint, jump, using, hitSlowdown, fastMath;

    public EmulationTracker(final PlayerData data) {
        super(data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (!(packet instanceof PacketPlayClientFlying)) return;

        final Motion realMotion = new Motion(data.getPositionTracker().getDeltaX(), 0.0D, data.getPositionTracker().getDeltaZ());

        // Set the lowest offset
        this.distance = Double.MAX_VALUE;

        iteration: {
            // Loop through the combos
            for (int f = -1; f < 2; f++) {
                for (int s = -1; s < 2; s++) {
                    for (int sp = 0; sp < 2; sp++) {
                        for (int jp = 0; jp < 2; jp++) {
                            for (int ui = 0; ui < 2; ui++) {
                                for (int hs = 0; hs < 2; hs++) {
                                    for (int fm = 0; fm < 2; fm++) {
                                        final boolean sprint = sp == 0;
                                        final boolean jump = jp == 0;
                                        final boolean using = ui == 0;
                                        final boolean hitSlowdown = hs == 0;
                                        final boolean fastMath = fm == 1;
                                        final boolean ground = data.getPositionTracker().isLastOnGround();
                                        final boolean sneaking = data.getActionTracker().isSneaking();

                                        if (f <= 0.0F && sprint && ground) continue;

                                        float forward = f;
                                        float strafe = s;

                                        if (using) {
                                            forward *= 0.2D;
                                            strafe *= 0.2D;
                                        }

                                        if (sneaking) {
                                            forward *= (float) 0.3D;
                                            strafe *= (float) 0.3D;
                                        }

                                        forward *= 0.98F;
                                        strafe *= 0.98F;

                                        final Motion motion = new Motion(
                                                data.getPositionTracker().getLastDeltaX(),
                                                0.0D,
                                                data.getPositionTracker().getLastDeltaZ()
                                        );

                                        if (data.getPositionTracker().isLastLastOnGround()) {
                                            motion.getX().multiply(0.6F * 0.91F);
                                            motion.getZ().multiply(0.6F * 0.91F);
                                        } else {
                                            motion.getX().multiply(0.91F);
                                            motion.getZ().multiply(0.91F);
                                        }

                                        data.getVelocityTracker().getActions().forEach(action -> action.accept(motion));

                                        if (hitSlowdown) {
                                            motion.getX().multiply(0.6D);
                                            motion.getZ().multiply(0.6D);
                                        }

                                        if (Math.abs(motion.getX().get()) < 0.005D) motion.getX().set(0.0D);
                                        if (Math.abs(motion.getZ().get()) < 0.005D) motion.getZ().set(0.0D);

                                        if (jump && sprint) {
                                            final float radians = data.getRotationTracker().getYaw() * 0.017453292F;

                                            motion.getX().subtract(sin(fastMath, radians) * 0.2F);
                                            motion.getZ().add(cos(fastMath, radians) * 0.2F);
                                        }

                                        float friction = 0.91F;
                                        if (data.getPositionTracker().isLastOnGround()) friction *= 0.6F;

                                        final float moveSpeed = PlayerUtil.getAttributeSpeed(data, sprint);
                                        final float moveFlyingFriction;

                                        if (ground) {
                                            final float moveSpeedMultiplier = 0.16277136F / (friction * friction * friction);

                                            moveFlyingFriction = moveSpeed * moveSpeedMultiplier;
                                        } else {
                                            moveFlyingFriction = (float) (sprint
                                                    ? ((double) 0.02F + (double) 0.02F * 0.3D)
                                                    : 0.02F);
                                        }

                                        final float[] moveFlying = this.moveFlying(forward, strafe, moveFlyingFriction, fastMath);

                                        motion.getX().add(moveFlying[0]);
                                        motion.getZ().add(moveFlying[1]);

                                        motion.getY().set(0.0D);

                                        final double distance = realMotion.distanceSquared(motion);

                                        // Set the lowest distance outcome
                                        if (distance < this.distance) {
                                            this.distance = distance;
                                            this.motion = motion.clone();

                                            this.sprint = sprint;
                                            this.jump = jump;
                                            this.using = using;
                                            this.hitSlowdown = hitSlowdown;
                                            this.fastMath = fastMath;

                                            if (distance < 1e-14) break iteration;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private float[] moveFlying(final float moveForward, final float moveStrafe, final float friction, final boolean fastMath) {
        float diagonal = moveStrafe * moveStrafe + moveForward * moveForward;

        float moveFlyingFactorX = 0.0F;
        float moveFlyingFactorZ = 0.0F;

        if (diagonal >= 1.0E-4F) {
            diagonal = MathHelper.sqrt_float(diagonal);

            if (diagonal < 1.0F) {
                diagonal = 1.0F;
            }

            diagonal = friction / diagonal;

            final float strafe = moveStrafe * diagonal;
            final float forward = moveForward * diagonal;

            final float rotationYaw = data.getRotationTracker().getYaw();

            final float f1 = sin(fastMath, rotationYaw * (float) Math.PI / 180.0F);
            final float f2 = cos(fastMath, rotationYaw * (float) Math.PI / 180.0F);

            final float factorX = strafe * f2 - forward * f1;
            final float factorZ = forward * f2 + strafe * f1;

            moveFlyingFactorX = factorX;
            moveFlyingFactorZ = factorZ;
        }

        return new float[] {
                moveFlyingFactorX,
                moveFlyingFactorZ
        };
    }

    private float sin(final boolean fastMath, final float yaw) {
        return fastMath ? OptiFineShitMath.sin(yaw) : MathHelper.sin(yaw);
    }

    private float cos(final boolean fastMath, final float yaw) {
        return fastMath ? OptiFineShitMath.cos(yaw) : MathHelper.cos(yaw);
    }
}
